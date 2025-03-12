package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.showAsset.CreateShowAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.showAsset.ShowAssetPermissionDTO;

import java.util.List;

public interface IShowAssetPermissionService {
    ShowAssetPermissionDTO createPermission(CreateShowAssetPermissionDTO dto);
    ShowAssetPermissionDTO getPermission(String showTypeID, String assetTypeID);
    List<ShowAssetPermissionDTO> getAllPermissions();
    ShowAssetPermissionDTO updatePermission(CreateShowAssetPermissionDTO dto);
    void deletePermission(String showTypeID, String assetTypeID);
}
