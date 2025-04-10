package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnAssetRequestDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnedAssetDTO;

import java.util.List;

public interface IReturnAssetService {
    void returnAsset(ReturnAssetRequestDTO dto);
    List<ReturnedAssetDTO> getAllReturnedAssets();
}
