package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.AssetDTO;

import java.util.List;

public interface IAssetService {
    List<AssetDTO> getAllAsset();
    List<AssetDTO> getAssetByName(String name);
}
