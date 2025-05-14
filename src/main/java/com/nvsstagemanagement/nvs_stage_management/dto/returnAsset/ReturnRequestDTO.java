package com.nvsstagemanagement.nvs_stage_management.dto.returnAsset;

import lombok.Data;

@Data
public class ReturnRequestDTO {
    private String assetId;
    private String taskId;
    private String description;
    private String conditionNote;
    private String imageUrl;
}

