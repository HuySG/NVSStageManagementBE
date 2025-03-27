package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetTypeDTO;

import java.util.List;

public interface IAssetTypeService {
    List<AssetTypeDTO> getAllAssetTypesWithCategories();
    AssetTypeDTO getAssetTypeById(String id);
    AssetTypeDTO createAssetType(AssetTypeDTO assetTypeDTO);
    AssetTypeDTO updateAssetType(AssetTypeDTO assetTypeDTO);
    void deleteAssetType(String id);
}
