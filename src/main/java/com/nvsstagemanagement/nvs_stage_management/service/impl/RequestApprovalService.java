package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.request.AllocateAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.AllocationStatus;
import com.nvsstagemanagement.nvs_stage_management.enums.BorrowedAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.enums.RequestAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.IRequestApprovalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestApprovalService implements IRequestApprovalService {
    private final RequestAssetRepository requestAssetRepository;
    private final AssetRepository assetRepository;
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final AssetUsageHistoryRepository assetUsageHistoryRepository;
    private final RequestAssetAllocationRepository requestAssetAllocationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Allocate specific assets to a category-based request as manually specified by the AM.
     * Validates that all assigned assets belong to the requested categories,
     * do not conflict in time, and match the quantity requirements.
     *
     * @param requestId the request to allocate for
     * @param allocationDTOs list of asset-category mapping to assign
     * @return updated RequestAssetDTO after successful assignment
     */
    @Override
    public RequestAssetDTO allocateAssets(String requestId, List<AllocateAssetDTO> allocationDTOs) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        if (request.getEndTime().isBefore(Instant.now())) {
            throw new RuntimeException("Cannot allocate assets for an expired request.");
        }

        Map<String, Integer> requiredQuantities = request.getRequestAssetCategories().stream()
                .collect(Collectors.toMap(
                        rac -> rac.getCategory().getCategoryID(),
                        RequestAssetCategory::getQuantity
                ));

        Map<String, Long> allocatedCount = new HashMap<>();
        Map<String, String> assetToCategory = new HashMap<>();

        for (AllocateAssetDTO allocationDTO : allocationDTOs) {
            String categoryId = allocationDTO.getCategoryID();
            List<String> assetIds = allocationDTO.getAllocatedAssetIDs();

            for (String assetId : assetIds) {
                Asset asset = assetRepository.findById(assetId)
                        .orElseThrow(() -> new RuntimeException("Asset not found: " + assetId));

                if (asset.getCategory() == null || !asset.getCategory().getCategoryID().equals(categoryId)) {
                    throw new RuntimeException("Asset " + assetId + " does not belong to category " + categoryId);
                }

                if (borrowedAssetRepository.existsAssetConflict(assetId, request.getStartTime(), request.getEndTime())) {
                    throw new RuntimeException("Asset " + assetId + " is already booked during this time.");
                }

                assetToCategory.put(assetId, categoryId);
                allocatedCount.put(categoryId, allocatedCount.getOrDefault(categoryId, 0L) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : requiredQuantities.entrySet()) {
            String categoryId = entry.getKey();
            int required = entry.getValue();
            long allocated = allocatedCount.getOrDefault(categoryId, 0L);
            if (allocated < required) {
                throw new RuntimeException("Not enough assets allocated for category: " + categoryId);
            }
        }

        for (Map.Entry<String, String> entry : assetToCategory.entrySet()) {
            String assetId = entry.getKey();
            Asset asset = assetRepository.findById(assetId).orElseThrow();

            RequestAssetAllocation allocation = new RequestAssetAllocation();
            allocation.setAllocationId(UUID.randomUUID().toString());
            allocation.setAsset(asset);
            allocation.setCategory(asset.getCategory());
            allocation.setRequestAsset(request);
            allocation.setNote("Manually allocated by AM");
            allocation.setStatus(AllocationStatus.PREPARING);
            requestAssetAllocationRepository.save(allocation);

            BorrowedAsset borrowed = new BorrowedAsset();
            borrowed.setBorrowedID(UUID.randomUUID().toString());
            borrowed.setAsset(asset);
            borrowed.setTask(request.getTask());
            borrowed.setBorrowTime(Instant.now());
            borrowed.setStartTime(request.getStartTime());
            borrowed.setEndTime(request.getEndTime());
            borrowed.setStatus(BorrowedAssetStatus.BOOKED.name());
            borrowed.setDescription("Allocated for request " + requestId);
            borrowedAssetRepository.save(borrowed);

            AssetUsageHistory usage = new AssetUsageHistory();
            usage.setUsageID(UUID.randomUUID().toString());
            usage.setAsset(asset);
            usage.setStartDate(request.getStartTime());
            usage.setEndDate(request.getEndTime());
            usage.setStatus("In Use");

            if (request.getTask() != null && request.getTask().getMilestone() != null
                    && request.getTask().getMilestone().getProject() != null) {
                usage.setProject(request.getTask().getMilestone().getProject());
            } else {
                throw new RuntimeException("Project info missing for request.");
            }

            if (request.getCreateBy() != null) {
                userRepository.findById(request.getCreateBy()).ifPresent(usage::setUser);
            }

            assetUsageHistoryRepository.save(usage);
        }

        request.setStatus(RequestAssetStatus.AM_APPROVED.name());
        RequestAsset updated = requestAssetRepository.save(request);
        return modelMapper.map(updated, RequestAssetDTO.class);
    }


    /**
     * Automatically allocate available assets to a category-based request.
     * This method looks for available assets matching each requested category,
     * checks for time conflicts, and if enough assets are available, assigns them.
     *
     * Preconditions:
     * - Request must be in PENDING_AM status.
     * - Request must be category-based (not individual asset).
     * - Each category must have enough available assets.
     *
     * Post conditions:
     * - Creates BorrowedAsset records for each assigned asset.
     * - Saves AssetUsageHistory entries.
     * - Updates request status to AM_APPROVED.
     *
     * @param requestId the ID of the request to process
     * @return the updated RequestAssetDTO after assignment
     */
    @Transactional
    @Override
    public RequestAssetDTO autoAllocateAssets(String requestId) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getRequestAssetCategories() == null || request.getRequestAssetCategories().isEmpty()) {
            throw new IllegalStateException("Request is not category-based.");
        }

        if (request.getEndTime().isBefore(Instant.now())) {
            throw new RuntimeException("Request has expired.");
        }

        List<Asset> allAvailableAssets = assetRepository.findAll()
                .stream()
                .filter(asset -> request.getRequestAssetCategories()
                        .stream()
                        .anyMatch(cat -> cat.getCategory().getCategoryID().equals(asset.getCategory().getCategoryID())))
                .filter(asset -> !borrowedAssetRepository.existsAssetConflict(
                        asset.getAssetID(), request.getStartTime(), request.getEndTime()))
                .collect(Collectors.toList());

        Map<String, List<Asset>> groupedByCategory = allAvailableAssets.stream()
                .collect(Collectors.groupingBy(asset -> asset.getCategory().getCategoryID()));

        for (RequestAssetCategory rac : request.getRequestAssetCategories()) {
            List<Asset> assets = groupedByCategory.getOrDefault(rac.getCategory().getCategoryID(), new ArrayList<>());
            if (assets.size() < rac.getQuantity()) {
                throw new RuntimeException("Not enough assets for category: " + rac.getCategory().getName());
            }

            for (int i = 0; i < rac.getQuantity(); i++) {
                Asset asset = assets.get(i);

                BorrowedAsset borrowed = new BorrowedAsset();
                borrowed.setBorrowedID(UUID.randomUUID().toString());
                borrowed.setAsset(asset);
                borrowed.setTask(request.getTask());
                borrowed.setBorrowTime(Instant.now());
                borrowed.setStartTime(request.getStartTime());
                borrowed.setEndTime(request.getEndTime());
                borrowed.setStatus(BorrowedAssetStatus.BOOKED.name());
                borrowed.setDescription("Auto allocated asset for request: " + requestId);
                borrowedAssetRepository.save(borrowed);

                AssetUsageHistory usage = new AssetUsageHistory();
                usage.setUsageID(UUID.randomUUID().toString());
                usage.setAsset(asset);
                usage.setStartDate(request.getStartTime());
                usage.setEndDate(request.getEndTime());
                usage.setStatus("In Use");

                if (request.getTask() != null && request.getTask().getMilestone() != null
                        && request.getTask().getMilestone().getProject() != null) {
                    usage.setProject(request.getTask().getMilestone().getProject());
                }

                if (request.getCreateBy() != null) {
                    userRepository.findById(request.getCreateBy()).ifPresent(usage::setUser);
                }

                assetUsageHistoryRepository.save(usage);
            }
        }

        request.setStatus(RequestAssetStatus.AM_APPROVED.name());
        requestAssetRepository.save(request);

        return modelMapper.map(request, RequestAssetDTO.class);
    }
}
