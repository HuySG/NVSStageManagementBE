package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.asset.UpdateAssetDTO;

import java.util.List;

public interface IAssetService {
    List<AssetDTO> getAllAsset();
    List<AssetDTO> getAssetByName(String name);
    AssetDTO createAsset(AssetDTO assetDTO);
    AssetDTO updateAsset(UpdateAssetDTO updateAssetDTO);
}
