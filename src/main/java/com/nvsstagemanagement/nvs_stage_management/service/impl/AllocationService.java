package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.allocation.AllocationDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.allocation.AssetUsageHistoryDTO;
import com.nvsstagemanagement.nvs_stage_management.model.AssetUsageHistory;
import com.nvsstagemanagement.nvs_stage_management.model.RequestAssetAllocation;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetUsageHistoryRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.RequestAssetAllocationRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.RequestAssetRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IAllocationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllocationService implements IAllocationService {
    private final RequestAssetRepository requestAssetRepository;
    private final RequestAssetAllocationRepository requestAssetAllocationRepository;
    private final AssetUsageHistoryRepository assetUsageHistoryRepository;
    private final AssetRepository assetRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<AllocationDTO> getAllocationDetails(String requestId) {
        // Lấy danh sách record RequestAssetAllocation theo requestId
        List<RequestAssetAllocation> allocations = requestAssetAllocationRepository.findByRequestAsset_RequestId(requestId);
        return allocations.stream().map(allocation -> {
            AllocationDTO dto = new AllocationDTO();
            dto.setAllocationId(allocation.getAllocationId());
            dto.setCategoryID(allocation.getCategory().getCategoryID());
            dto.setCategoryName(allocation.getCategory().getName());
            dto.setAssetID(allocation.getAsset().getAssetID());
            dto.setAssetName(allocation.getAsset().getAssetName());
            // Lấy lịch sử sử dụng của asset từ bảng AssetUsageHistory
            List<AssetUsageHistory> histories = assetUsageHistoryRepository.findByAssetOrderByStartDateDesc(allocation.getAsset());
            List<AssetUsageHistoryDTO> historyDTOs = histories.stream().map(history -> {
                AssetUsageHistoryDTO hdto = new AssetUsageHistoryDTO();
                hdto.setUsageID(history.getUsageID());
                hdto.setStartDate(history.getStartDate());
                hdto.setEndDate(history.getEndDate());
                hdto.setStatus(history.getStatus());
                if (history.getProject() != null) {
                    hdto.setProjectID(history.getProject().getProjectID());
                    hdto.setProjectName(history.getProject().getTitle());
                }
                return hdto;
            }).collect(Collectors.toList());
            dto.setUsageHistory(historyDTOs);
            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public List<AssetUsageHistoryDTO> getUsageHistoryByAsset(String assetId) {
        // Lấy asset từ repository
        var asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found: " + assetId));
        List<AssetUsageHistory> histories = assetUsageHistoryRepository.findByAssetOrderByStartDateDesc(asset);
        return histories.stream().map(history -> {
            AssetUsageHistoryDTO hdto = new AssetUsageHistoryDTO();
            hdto.setUsageID(history.getUsageID());
            hdto.setStartDate(history.getStartDate());
            hdto.setEndDate(history.getEndDate());
            hdto.setStatus(history.getStatus());
            if(history.getProject() != null){
                hdto.setProjectID(history.getProject().getProjectID());
                hdto.setProjectName(history.getProject().getTitle());
            }
            return hdto;
        }).collect(Collectors.toList());
    }
}
