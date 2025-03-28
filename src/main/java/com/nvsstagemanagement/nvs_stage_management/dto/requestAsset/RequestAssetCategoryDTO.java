package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import lombok.Data;

@Data
public class RequestAssetCategoryDTO {
    private String categoryID;
    private String name;
    private Integer quantity;
}
