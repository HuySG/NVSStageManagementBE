package com.nvsstagemanagement.nvs_stage_management.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AllocateAssetDTO {
    private String categoryID;
    private List<String> allocatedAssetIDs;
}
