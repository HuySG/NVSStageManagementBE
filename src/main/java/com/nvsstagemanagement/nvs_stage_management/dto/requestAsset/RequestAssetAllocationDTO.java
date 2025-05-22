package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import lombok.Data;

@Data
public class RequestAssetAllocationDTO {
    private String allocationId;
    private AssetDTO asset;
    private String categoryId;
    private String status;
    private String note;
}
