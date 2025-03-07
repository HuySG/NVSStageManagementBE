package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.projectAsset.CreateProjectAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.projectAsset.ProjectAssetPermissionDTO;

import java.util.List;

public interface IProjectAssetPermissionService {
    ProjectAssetPermissionDTO createPermission(CreateProjectAssetPermissionDTO dto);
    ProjectAssetPermissionDTO getPermission(String projectTypeID, String assetTypeID);
    List<ProjectAssetPermissionDTO> getAllPermissions();
    ProjectAssetPermissionDTO updatePermission(CreateProjectAssetPermissionDTO dto);
    void deletePermission(String projectTypeID, String assetTypeID);
}
