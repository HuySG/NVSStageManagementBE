package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.allocation.AllocationDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.allocation.AssetUsageHistoryDTO;

import java.util.List;

public interface IAllocationService {
    List<AllocationDTO> getAllocationDetails(String requestId);
    List<AssetUsageHistoryDTO> getUsageHistoryByAsset(String assetId);
    void saveBeforeImagesFromFirebase(String allocationId, List<String> imageUrls);
}
