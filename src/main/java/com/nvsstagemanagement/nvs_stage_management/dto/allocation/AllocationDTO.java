package com.nvsstagemanagement.nvs_stage_management.dto.allocation;

import lombok.Data;

import java.util.List;

@Data
public class AllocationDTO {
    private String allocationId;
    private String categoryID;
    private String categoryName;
    private String assetID;
    private String assetName;
    private List<AssetUsageHistoryDTO> usageHistory;
}
