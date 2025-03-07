package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.CreateRequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.UpdateRequestAssetStatusDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.RequestAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import com.nvsstagemanagement.nvs_stage_management.model.RequestAsset;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.RequestAssetRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IRequestAssetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestAssetService implements IRequestAssetService {
    private final RequestAssetRepository requestAssetRepository;
    private final AssetRepository assetRepository;
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
}
