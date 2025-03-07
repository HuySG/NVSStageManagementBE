package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.projectAsset.CreateProjectAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.projectAsset.ProjectAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.model.ProjectAssetPermission;
import com.nvsstagemanagement.nvs_stage_management.model.ProjectAssetPermissionId;
import com.nvsstagemanagement.nvs_stage_management.repository.ProjectAssetPermissionRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IProjectAssetPermissionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectAssetPermissionService implements IProjectAssetPermissionService {
    private final ProjectAssetPermissionRepository permissionRepository;
    private final ModelMapper modelMapper;

    public ProjectAssetPermissionDTO createPermission(CreateProjectAssetPermissionDTO dto) {
        ProjectAssetPermissionId id = new ProjectAssetPermissionId(dto.getProjectTypeID(), dto.getAssetTypeID());
        if (permissionRepository.existsById(id)) {
            throw new RuntimeException("Permission already exists for this project type and asset type.");
        }
        ProjectAssetPermission permission = modelMapper.map(dto, ProjectAssetPermission.class);
        permission.setId(id);
        ProjectAssetPermission saved = permissionRepository.save(permission);
        return modelMapper.map(saved, ProjectAssetPermissionDTO.class);
    }

    public ProjectAssetPermissionDTO getPermission(String projectTypeID, String assetTypeID) {
        ProjectAssetPermissionId id = new ProjectAssetPermissionId(projectTypeID, assetTypeID);
        ProjectAssetPermission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found."));
        return modelMapper.map(permission, ProjectAssetPermissionDTO.class);
    }

    public List<ProjectAssetPermissionDTO> getAllPermissions() {
        List<ProjectAssetPermission> all = permissionRepository.findAll();
        return all.stream()
                .map(p -> modelMapper.map(p, ProjectAssetPermissionDTO.class))
                .collect(Collectors.toList());
    }

    public ProjectAssetPermissionDTO updatePermission(CreateProjectAssetPermissionDTO dto) {
        ProjectAssetPermissionId id = new ProjectAssetPermissionId(dto.getAssetTypeID(), dto.getProjectTypeID());
        ProjectAssetPermission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found."));
        permission.setAllowed(dto.isAllowed());
        permission.setIsEssential(dto.isEssential());
        ProjectAssetPermission updated = permissionRepository.save(permission);
        return modelMapper.map(updated, ProjectAssetPermissionDTO.class);
    }

    public void deletePermission(String projectTypeID, String assetTypeID) {
        ProjectAssetPermissionId id = new ProjectAssetPermissionId(projectTypeID, assetTypeID);
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("Permission not found.");
        }
        permissionRepository.deleteById(id);
    }
}
