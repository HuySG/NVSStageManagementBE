package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.projectAsset.ProjectAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.model.ProjectAssetPermission;
import com.nvsstagemanagement.nvs_stage_management.model.ProjectAssetPermissionId;
import com.nvsstagemanagement.nvs_stage_management.repository.ProjectAssetPermissionRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IProjectAssetPermissionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectAssetPermissionService implements IProjectAssetPermissionService {
    private final ProjectAssetPermissionRepository permissionRepository;
    private final ModelMapper modelMapper;
    public ProjectAssetPermissionDTO createPermission(ProjectAssetPermissionDTO dto) {
        ProjectAssetPermissionId id = new ProjectAssetPermissionId();
        id.setProjectTypeID(dto.getProjectTypeID());
        id.setAssetTypeID(dto.getAssetTypeID());
        if (permissionRepository.existsById(id)) {
            throw new RuntimeException("Permission already exists for this project type and asset type.");
        }
        ProjectAssetPermission permission = new ProjectAssetPermission();
        permission.setId(id);
        ProjectAssetPermission saved = permissionRepository.save(permission);
        return modelMapper.map(saved, ProjectAssetPermissionDTO.class);
    }
}
