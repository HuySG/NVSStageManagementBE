package com.nvsstagemanagement.nvs_stage_management.dto.returnAsset;

import lombok.Data;

@Data
public class ReturnAssetRequestDTO {
    private String taskID;
    private String assetID;
    private String description;
}
