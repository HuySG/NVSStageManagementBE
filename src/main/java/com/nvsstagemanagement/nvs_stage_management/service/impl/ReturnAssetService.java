package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnAssetRequestDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.BorrowedAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.IReturnAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnAssetService implements IReturnAssetService {
    private static final BigDecimal LATE_FEE_PER_DAY = BigDecimal.valueOf(100_000);
    
    private final ReturnedAssetRepository returnedAssetRepository;
    private final AssetRepository assetRepository;
    private final TaskRepository taskRepository;
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final AssetUsageHistoryRepository assetUsageHistoryRepository;

    @Override
    @Transactional
    public void returnAsset(ReturnAssetRequestDTO dto) {
        Asset asset = findAsset(dto.getAssetID());
        Task task = findTask(dto.getTaskID());
        BorrowedAsset borrowed = findActiveBorrowedAsset(asset, task);
        
        validateReturn(asset, task);
        
        ReturnedAsset returnedAsset = createReturnedAsset(dto, asset, task, borrowed);
        updateAssetStatus(asset, borrowed);
        updateUsageHistory(asset, task);
    }

    private Asset findAsset(String assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
    }

    private Task findTask(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    private BorrowedAsset findActiveBorrowedAsset(Asset asset, Task task) {
        return borrowedAssetRepository.findByAsset_AssetIDAndTask_TaskIDAndStatus(
                asset.getAssetID(),
                task.getTaskID(),
                BorrowedAssetStatus.IN_USE.name()
        ).orElseThrow(() -> new RuntimeException("No active borrowed record found"));
    }

    private void validateReturn(Asset asset, Task task) {
        if (returnedAssetRepository.existsByAssetIDAndTaskID(asset.getAssetID(), task.getTaskID())) {
            throw new RuntimeException("Asset already returned for this task.");
        }
    }

    private ReturnedAsset createReturnedAsset(ReturnAssetRequestDTO dto, Asset asset, Task task, BorrowedAsset borrowed) {
        ReturnedAsset returnedAsset = new ReturnedAsset();
        returnedAsset.setReturnedAssetID(UUID.randomUUID().toString());
        returnedAsset.setAssetID(asset);
        returnedAsset.setTaskID(task);
        returnedAsset.setReturnTime(Instant.now());
        returnedAsset.setDescription(dto.getDescription());
        calculateLateFee(returnedAsset, borrowed);
        return returnedAssetRepository.save(returnedAsset);
    }

    private void calculateLateFee(ReturnedAsset returnedAsset, BorrowedAsset borrowed) {
        Instant now = Instant.now();
        Instant expectedReturn = borrowed.getEndTime();
        if (now.isAfter(expectedReturn)) {
            long lateDays = Duration.between(expectedReturn, now).toDays();
            if (lateDays > 0) {
                returnedAsset.setLatePenaltyFee(LATE_FEE_PER_DAY.multiply(BigDecimal.valueOf(lateDays)));
            }
        }
    }

    private void updateAssetStatus(Asset asset, BorrowedAsset borrowed) {
        borrowed.setStatus(BorrowedAssetStatus.RETURNED.name());
        borrowedAssetRepository.save(borrowed);
        
        asset.setStatus("AVAILABLE");
        assetRepository.save(asset);
    }

    private void updateUsageHistory(Asset asset, Task task) {
        AssetUsageHistory usage = assetUsageHistoryRepository
                .findByAsset_AssetIDAndProject_ProjectID(
                        asset.getAssetID(),
                        task.getMilestone().getProject().getProjectID()
                )
                .orElseThrow(() -> new RuntimeException("Usage history not found."));
        usage.setStatus("Returned");
        assetUsageHistoryRepository.save(usage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnedAssetDTO> getAllReturnedAssets() {
        return returnedAssetRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ReturnedAssetDTO convertToDTO(ReturnedAsset asset) {
        ReturnedAssetDTO dto = new ReturnedAssetDTO();
        dto.setReturnedAssetID(asset.getReturnedAssetID());
        dto.setReturnTime(asset.getReturnTime());
        dto.setDescription(asset.getDescription());
        
        if (asset.getTaskID() != null) {
            dto.setTaskID(asset.getTaskID().getTaskID());
        }
        if (asset.getAssetID() != null) {
            dto.setAssetID(asset.getAssetID().getAssetID());
        }
        return dto;
    }
}