package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.exception.NotEnoughAssetException;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.CreateRequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.UpdateRequestAssetStatusDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.RequestAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import com.nvsstagemanagement.nvs_stage_management.model.AssetUsageHistory;
import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAsset;
import com.nvsstagemanagement.nvs_stage_management.model.RequestAsset;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.BorrowedAssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.RequestAssetRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IRequestAssetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestAssetService implements IRequestAssetService {
    private final RequestAssetRepository requestAssetRepository;
    private final AssetRepository assetRepository;
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<RequestAssetDTO> getAllRequest() {
        List<RequestAsset> requests = requestAssetRepository.findAll();
        return requests.stream()
                .map(request -> modelMapper.map(request, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestAssetDTO> createRequest(List<CreateRequestAssetDTO> dtos) {
        List<RequestAssetDTO> responses = new ArrayList<>();
        for (CreateRequestAssetDTO dto : dtos) {
            Asset asset = assetRepository.findById(dto.getAssetID())
                    .orElseThrow(() -> new RuntimeException("Asset not found: " + dto.getAssetID()));
            RequestAsset requestAsset = modelMapper.map(dto, RequestAsset.class);
            if (requestAsset.getRequestId() == null || requestAsset.getRequestId().trim().isEmpty())
                requestAsset.setRequestId(UUID.randomUUID().toString());
            requestAsset.setStatus(RequestAssetStatus.PENDING_LEADER.toString());
            requestAsset.setAsset(asset);
            requestAsset.setRequestTime(LocalDateTime.now().toInstant(ZoneOffset.UTC));
            RequestAsset savedRequest = requestAssetRepository.save(requestAsset);
            responses.add(modelMapper.map(savedRequest, RequestAssetDTO.class));
        }
        return responses;
    }

    @Override
    public RequestAssetDTO getRequestById(String id) {
       Optional< RequestAsset> requestAsset = requestAssetRepository.findById(id);
       return modelMapper.map(requestAsset, RequestAssetDTO.class);
    }

    @Override
    public RequestAssetDTO updateRequestAssetStatus(UpdateRequestAssetStatusDTO dto) {
        RequestAsset request = requestAssetRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found: " + dto.getRequestId()));
        request.setStatus(dto.getStatus());
        RequestAsset updated = requestAssetRepository.save(request);
        return modelMapper.map(updated, RequestAssetDTO.class);
    }
    @Override
    public List<RequestAssetDTO> getRequestsForLeader(String departmentId, String status) {
        List<RequestAsset> requests = requestAssetRepository.findRequestsForDepartmentLeader(departmentId, status);
        return requests.stream()
                .map(request -> modelMapper.map(request, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<RequestAssetDTO> getRequestsByUser(String userId) {
        List<RequestAsset> requests = requestAssetRepository.findByUserId(userId);
        return requests.stream()
                .map(r -> modelMapper.map(r, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<RequestAssetDTO> getRequestsForAssetManager() {
        List<String> allowedStatuses = Arrays.asList("PENDING_AM", "AM_APPROVED", "REJECTED", "CANCELLED");
        List<RequestAsset> requests = requestAssetRepository.findByStatusIn(allowedStatuses);
        return requests.stream()
                .map(request -> modelMapper.map(request, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public RequestAssetDTO acceptRequest(String requestId) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        if (RequestAssetStatus.AM_APPROVED.name().equals(request.getStatus())) {
            throw new NotEnoughAssetException("Request already accepted.");
        }
        Asset asset = assetRepository.findById(request.getAsset().getAssetID())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + request.getAsset().getAssetID()));
        String assetTypeID = asset.getAssetType().getAssetTypeID();
        int totalAssets = assetRepository.countByAssetType_AssetTypeID(assetTypeID);
        int overlappingCount = borrowedAssetRepository.countOverlapping(assetTypeID, request.getStartTime(), request.getEndTime());
        int availableAssets = totalAssets - overlappingCount;
        if (request.getQuantity() > availableAssets) {
            throw new NotEnoughAssetException("Not enough assets available. Requested: "
                    + request.getQuantity() + ", available: " + availableAssets);
        }

        List<Asset> availableAssetList = assetRepository.findAvailableAssets(assetTypeID, request.getStartTime(), request.getEndTime());

        int count = request.getQuantity();
        for (Asset availableAsset : availableAssetList) {
            if (count-- <= 0) break;
            BorrowedAsset borrowed = new BorrowedAsset();
            borrowed.setBorrowedID(UUID.randomUUID().toString());
            borrowed.setAsset(availableAsset);
            borrowed.setTask(request.getTask());
            borrowed.setBorrowTime(LocalDateTime.now());
            borrowed.setEndTime(request.getEndTime());
            borrowed.setQuantity(1);
            borrowed.setDescription("Accepted request " + requestId);
            borrowedAssetRepository.save(borrowed);
        }

        request.setStatus(RequestAssetStatus.AM_APPROVED.name());
        RequestAsset updatedRequest = requestAssetRepository.save(request);
        return modelMapper.map(updatedRequest, RequestAssetDTO.class);
    }

}
