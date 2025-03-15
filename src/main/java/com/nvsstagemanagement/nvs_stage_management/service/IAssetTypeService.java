package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetTypeDTO;

import java.util.List;

public interface IAssetTypeService {
    List<AssetTypeDTO> getAllAssetTypesWithCategories();
}
