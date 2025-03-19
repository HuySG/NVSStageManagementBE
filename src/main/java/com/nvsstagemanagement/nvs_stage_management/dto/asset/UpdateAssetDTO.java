package com.nvsstagemanagement.nvs_stage_management.dto.asset;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UpdateAssetDTO extends AssetDTO{
    private String categoryID;
    private String assetTypeID;

}
