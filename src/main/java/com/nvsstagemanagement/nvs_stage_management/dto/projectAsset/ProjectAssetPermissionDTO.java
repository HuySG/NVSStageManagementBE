package com.nvsstagemanagement.nvs_stage_management.dto.projectAsset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAssetPermissionDTO {
    private Integer projectTypeID;
    private String assetTypeID;
    private boolean allowed;
    private boolean isEssential;
}
