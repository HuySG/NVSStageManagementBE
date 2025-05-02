package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.projectAsset.CreateProjectAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.projectAsset.ProjectAssetPermissionDTO;

import java.util.List;

public interface IProjectAssetPermissionService {
    ProjectAssetPermissionDTO createPermission(CreateProjectAssetPermissionDTO dto);
    ProjectAssetPermissionDTO getPermission(Integer projectTypeID, String categoryID);
    List<ProjectAssetPermissionDTO> getAllPermissions();
    ProjectAssetPermissionDTO updatePermission(CreateProjectAssetPermissionDTO dto);
    void deletePermission(Integer projectTypeID, String categoryID);
}
