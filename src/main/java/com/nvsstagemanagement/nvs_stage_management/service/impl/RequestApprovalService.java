package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.request.AllocateAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetAllocationDTO;
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
    private final CategoryRepository categoryRepository;
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
    @Transactional
    public RequestAssetDTO allocateAssets(String requestId, List<AllocateAssetDTO> allocationDTOs) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        if (request.getEndTime().isBefore(Instant.now())) {
            throw new RuntimeException("Cannot allocate assets for an expired request.");
        }

        Map<String, Integer> requiredMap = request.getRequestAssetCategories().stream()
                .collect(Collectors.toMap(
                        rac -> rac.getCategory().getCategoryID(),
                        RequestAssetCategory::getQuantity
                ));

        Map<String, Long> allocatedCount = new HashMap<>();
        Map<String, List<String>> toAllocate = new LinkedHashMap<>();

        for (AllocateAssetDTO dto : allocationDTOs) {
            String catId = dto.getCategoryID();
            for (String assetId : dto.getAllocatedAssetIDs()) {
                Asset asset = assetRepository.findById(assetId)
                        .orElseThrow(() -> new RuntimeException("Asset not found: " + assetId));
                if (asset.getCategory() == null || !asset.getCategory().getCategoryID().equals(catId)) {
                    throw new RuntimeException("Asset " + assetId + " does not belong to category " + catId);
                }
                if (borrowedAssetRepository.existsAssetConflict(assetId, request.getStartTime(), request.getEndTime())) {
                    throw new RuntimeException("Asset " + assetId + " is already booked during this time.");
                }
                toAllocate.computeIfAbsent(catId, k -> new ArrayList<>()).add(assetId);
                allocatedCount.merge(catId, 1L, Long::sum);
            }
        }

        for (Map.Entry<String, List<String>> entry : toAllocate.entrySet()) {
            String catId = entry.getKey();
            for (String assetId : entry.getValue()) {
                Asset asset = assetRepository.findById(assetId).get();
                RequestAssetAllocation alloc = new RequestAssetAllocation();
                alloc.setAllocationId(UUID.randomUUID().toString());
                alloc.setAsset(asset);
                alloc.setCategory(asset.getCategory());
                alloc.setRequestAsset(request);
                alloc.setStatus(AllocationStatus.PREPARING);
                alloc.setNote("Manually allocated by AM");
                requestAssetAllocationRepository.save(alloc);

                BorrowedAsset borrowed = new BorrowedAsset();
                borrowed.setBorrowedID(UUID.randomUUID().toString());
                borrowed.setAsset(asset);
                borrowed.setTask(request.getTask());
                borrowed.setBorrowTime(Instant.now());
                borrowed.setStartTime(request.getStartTime());
                borrowed.setEndTime(request.getEndTime());
                borrowed.setStatus(BorrowedAssetStatus.PREPARING.name());
                borrowed.setDescription("Allocated for request " + requestId);
                borrowedAssetRepository.save(borrowed);

                AssetUsageHistory usage = new AssetUsageHistory();
                usage.setUsageID(UUID.randomUUID().toString());
                usage.setAsset(asset);
                usage.setStartDate(request.getStartTime());
                usage.setEndDate(request.getEndTime());
                usage.setStatus("In Use");
                usage.setProject(
                        Optional.ofNullable(request.getTask())
                                .map(Task::getMilestone)
                                .map(m -> m.getProject())
                                .orElseThrow(() -> new RuntimeException("Project info missing"))
                );
                Optional.ofNullable(request.getCreateBy())
                        .flatMap(userRepository::findById)
                        .ifPresent(usage::setUser);
                assetUsageHistoryRepository.save(usage);
            }
        }

        boolean anyShortage = false;
        for (Map.Entry<String, Integer> entry : requiredMap.entrySet()) {
            String catId = entry.getKey();
            int required = entry.getValue();
            long allocated = allocatedCount.getOrDefault(catId, 0L);
            int shortage = required - (int) allocated;
            if (shortage > 0) {
                anyShortage = true;
                Category cat = categoryRepository.findById(catId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + catId));
                for (int i = 0; i < shortage; i++) {
                    RequestAssetAllocation waiting = new RequestAssetAllocation();
                    waiting.setAllocationId(UUID.randomUUID().toString());
                    waiting.setAsset(null);
                    waiting.setCategory(cat);
                    waiting.setRequestAsset(request);
                    waiting.setStatus(AllocationStatus.PENDING);
                    waiting.setNote("Chờ cấp phát do thiếu asset");
                    requestAssetAllocationRepository.save(waiting);
                }
            }
        }
        if (anyShortage) {
            request.setStatus(RequestAssetStatus.PARTIALLY_ALLOCATED.name());
        } else {
            request.setStatus(RequestAssetStatus.FULLY_ALLOCATED.name());
        }

        RequestAsset saved = requestAssetRepository.save(request);

        RequestAssetDTO dto = modelMapper.map(saved, RequestAssetDTO.class);

        List<RequestAssetAllocation> allocationEntities =
                requestAssetAllocationRepository.findByRequestAsset(saved);

        List<RequestAssetAllocationDTO> allocDtos = allocationEntities.stream()
                .map(a -> modelMapper.map(a, RequestAssetAllocationDTO.class))
                .collect(Collectors.toList());
        dto.setAllocations(allocDtos);

        return dto;
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
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        if (request.getRequestAssetCategories() == null || request.getRequestAssetCategories().isEmpty()) {
            throw new IllegalStateException("Request is not category-based.");
        }

        if (request.getEndTime().isBefore(Instant.now())) {
            throw new RuntimeException("Request has expired.");
        }

        Map<String, Integer> requiredMap = request.getRequestAssetCategories().stream()
                .collect(Collectors.toMap(
                        rac -> rac.getCategory().getCategoryID(),
                        RequestAssetCategory::getQuantity
                ));

        Map<String, Long> allocatedCount = new HashMap<>();

        List<Asset> availableAssets = assetRepository.findAll().stream()
                .filter(asset -> requiredMap.containsKey(asset.getCategory().getCategoryID()))
                .filter(asset -> !borrowedAssetRepository.existsAssetConflict(
                        asset.getAssetID(), request.getStartTime(), request.getEndTime()))
                .collect(Collectors.toList());

        Map<String, Queue<Asset>> assetsByCategory = availableAssets.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategory().getCategoryID(),
                        Collectors.toCollection(LinkedList::new)
                ));

        for (RequestAssetCategory rac : request.getRequestAssetCategories()) {
            String catId = rac.getCategory().getCategoryID();
            int needed = rac.getQuantity();
            Queue<Asset> queue = assetsByCategory.getOrDefault(catId, new LinkedList<>());
            long count = 0;
            while (count < needed && !queue.isEmpty()) {
                Asset asset = queue.poll();

                RequestAssetAllocation alloc = new RequestAssetAllocation();
                alloc.setAllocationId(UUID.randomUUID().toString());
                alloc.setAsset(asset);
                alloc.setCategory(asset.getCategory());
                alloc.setRequestAsset(request);
                alloc.setStatus(AllocationStatus.PREPARING);
                alloc.setNote("Auto allocated");
                requestAssetAllocationRepository.save(alloc);

                BorrowedAsset borrowed = new BorrowedAsset();
                borrowed.setBorrowedID(UUID.randomUUID().toString());
                borrowed.setAsset(asset);
                borrowed.setTask(request.getTask());
                borrowed.setBorrowTime(Instant.now());
                borrowed.setStartTime(request.getStartTime());
                borrowed.setEndTime(request.getEndTime());
                borrowed.setStatus(BorrowedAssetStatus.PREPARING.name());
                borrowed.setDescription("Auto allocation for request " + requestId);
                borrowedAssetRepository.save(borrowed);

                AssetUsageHistory usage = new AssetUsageHistory();
                usage.setUsageID(UUID.randomUUID().toString());
                usage.setAsset(asset);
                usage.setStartDate(request.getStartTime());
                usage.setEndDate(request.getEndTime());
                usage.setStatus("In Use");
                usage.setProject(request.getTask().getMilestone().getProject());
                userRepository.findById(request.getCreateBy()).ifPresent(usage::setUser);
                assetUsageHistoryRepository.save(usage);

                count++;
            }
            allocatedCount.put(catId, count);
        }

        boolean anyShortage = false;
        for (Map.Entry<String, Integer> entry : requiredMap.entrySet()) {
            String catId = entry.getKey();
            int required = entry.getValue();
            long alloc = allocatedCount.getOrDefault(catId, 0L);
            int shortage = required - (int) alloc;
            if (shortage > 0) {
                anyShortage = true;
                Category cat = categoryRepository.findById(catId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + catId));
                for (int i = 0; i < shortage; i++) {
                    RequestAssetAllocation waiting = new RequestAssetAllocation();
                    waiting.setAllocationId(UUID.randomUUID().toString());
                    waiting.setAsset(null);
                    waiting.setCategory(cat);
                    waiting.setRequestAsset(request);
                    waiting.setStatus(AllocationStatus.PENDING);
                    waiting.setNote("Waiting for asset");
                    requestAssetAllocationRepository.save(waiting);
                }
            }
        }

        if (anyShortage) {
            request.setStatus(RequestAssetStatus.PARTIALLY_ALLOCATED.name());
        } else {
            request.setStatus(RequestAssetStatus.FULLY_ALLOCATED.name());
        }

        RequestAsset saved = requestAssetRepository.save(request);

        RequestAssetDTO dto = modelMapper.map(saved, RequestAssetDTO.class);
        List<RequestAssetAllocationDTO> allocDtos = requestAssetAllocationRepository
                .findByRequestAsset(saved)
                .stream()
                .map(a -> modelMapper.map(a, RequestAssetAllocationDTO.class))
                .collect(Collectors.toList());
        dto.setAllocations(allocDtos);

        return dto;
    }

}
