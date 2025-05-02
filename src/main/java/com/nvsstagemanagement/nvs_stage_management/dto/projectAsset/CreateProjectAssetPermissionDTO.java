package com.nvsstagemanagement.nvs_stage_management.dto.projectAsset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectAssetPermissionDTO {
    private Integer projectTypeID;
    private String categoryID;
    private boolean allowed;
    private boolean isEssential;
}
