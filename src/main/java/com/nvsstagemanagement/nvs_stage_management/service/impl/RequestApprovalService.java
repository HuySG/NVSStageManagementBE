package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.request.AllocateAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.RequestAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.IRequestApprovalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestApprovalService implements IRequestApprovalService {
    private final RequestAssetRepository requestAssetRepository;
    private final AssetRepository assetRepository;
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final AssetUsageHistoryRepository assetUsageHistoryRepository;

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public RequestAssetDTO allocateAssets(String requestId, List<AllocateAssetDTO> allocationDTOs) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        if (request.getRequestAssetCategories() == null || request.getRequestAssetCategories().isEmpty()) {
            throw new IllegalStateException("This is not a category-based request.");
        }

        for (AllocateAssetDTO allocationDTO : allocationDTOs) {
            List<String> categoryIDs = allocationDTO.getCategoryID();

            List<RequestAssetCategory> requestedCategories = request.getRequestAssetCategories()
                    .stream()
                    .filter(rac -> categoryIDs.contains(rac.getCategory().getCategoryID()))
                    .toList();

            if (requestedCategories.isEmpty()) {
                throw new RuntimeException("None of the categories " + categoryIDs + " are requested in this request.");
            }

            List<String> allocatedAssetIDs = allocationDTO.getAllocatedAssetIDs();

            for (String assetId : allocatedAssetIDs) {
                Asset asset = assetRepository.findById(assetId)
                        .orElseThrow(() -> new RuntimeException("Asset not found: " + assetId));

                if (asset.getCategory() == null || !categoryIDs.contains(asset.getCategory().getCategoryID())) {
                    throw new RuntimeException("Asset " + assetId + " does not belong to any of requested categories " + categoryIDs);
                }

                // Tạo record BorrowedAsset
                BorrowedAsset borrowed = new BorrowedAsset();
                borrowed.setBorrowedID(UUID.randomUUID().toString());
                borrowed.setAsset(asset);
                borrowed.setTask(request.getTask());
                borrowed.setBorrowTime(Instant.now());
                borrowed.setEndTime(request.getEndTime());
                borrowed.setDescription("Allocated asset for category request " + requestId);
                borrowedAssetRepository.save(borrowed);

                // Tạo AssetUsageHistory
                AssetUsageHistory usage = new AssetUsageHistory();
                usage.setUsageID(UUID.randomUUID().toString());
                usage.setAsset(asset);
                if (request.getTask() != null && request.getTask().getMilestone() != null
                        && request.getTask().getMilestone().getProject() != null) {
                    usage.setProject(request.getTask().getMilestone().getProject());
                } else {
                    throw new RuntimeException("Project information not found for request " + requestId);
                }
                if (request.getCreateBy() != null) {
                    User user = userRepository.findById(request.getCreateBy()).orElse(null);
                    usage.setUser(user);
                }
                usage.setStartDate(request.getStartTime());
                usage.setEndDate(request.getEndTime());
                usage.setStatus("In Use");
                assetUsageHistoryRepository.save(usage);
            }
        }

        request.setStatus(RequestAssetStatus.AM_APPROVED.toString());
        RequestAsset updatedRequest = requestAssetRepository.save(request);

        return modelMapper.map(updatedRequest, RequestAssetDTO.class);
    }

}
