package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.allocation.AllocationDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.allocation.AssetUsageHistoryDTO;

import java.util.List;

public interface IAllocationService {
    /**
     * Lấy chi tiết phân bổ asset và lịch sử sử dụng của các asset được phân bổ cho request danh mục.
     * @param requestId ID của request danh mục
     * @return Danh sách AllocationDTO chứa thông tin phân bổ và lịch sử sử dụng
     */
    List<AllocationDTO> getAllocationDetails(String requestId);

    /**
     * Lấy lịch sử sử dụng của một asset cụ thể.
     * @param assetId ID của asset
     * @return Danh sách AssetUsageHistoryDTO của asset đó
     */
    List<AssetUsageHistoryDTO> getUsageHistoryByAsset(String assetId);
}
