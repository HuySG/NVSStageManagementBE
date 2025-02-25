package com.nvsstagemanagement.nvs_stage_management.dto.asset;

import com.nvsstagemanagement.nvs_stage_management.dto.AssetTypeDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.CategoryDTO;
import com.nvsstagemanagement.nvs_stage_management.model.AssetType;
import com.nvsstagemanagement.nvs_stage_management.model.Category;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class UpdateAssetDTO extends AssetDTO{
    private String categoryID;
    private String assetTypeID;

}
