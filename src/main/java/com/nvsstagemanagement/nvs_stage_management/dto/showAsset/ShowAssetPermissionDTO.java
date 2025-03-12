package com.nvsstagemanagement.nvs_stage_management.dto.showAsset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowAssetPermissionDTO {
    private String projectTypeID;
    private String assetTypeID;
    private boolean allowed;
    private boolean isEssential;
}
