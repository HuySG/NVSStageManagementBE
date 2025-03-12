package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.showAsset.CreateShowAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.showAsset.ShowAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.model.ShowAssetPermission;
import com.nvsstagemanagement.nvs_stage_management.model.ShowAssetPermissionId;
import com.nvsstagemanagement.nvs_stage_management.repository.ShowAssetPermissionRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IShowAssetPermissionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowAssetPermissionService implements IShowAssetPermissionService {
    private final ShowAssetPermissionRepository permissionRepository;
    private final ModelMapper modelMapper;

    public ShowAssetPermissionDTO createPermission(CreateShowAssetPermissionDTO dto) {
        ShowAssetPermissionId id = new ShowAssetPermissionId(dto.getProjectTypeID(), dto.getAssetTypeID());
        if (permissionRepository.existsById(id)) {
            throw new RuntimeException("Permission already exists for this project type and asset type.");
        }
        ShowAssetPermission permission = modelMapper.map(dto, ShowAssetPermission.class);
        permission.setId(id);
        ShowAssetPermission saved = permissionRepository.save(permission);
        return modelMapper.map(saved, ShowAssetPermissionDTO.class);
    }

    public ShowAssetPermissionDTO getPermission(String showTypeID, String assetTypeID) {
        ShowAssetPermissionId id = new ShowAssetPermissionId(showTypeID, assetTypeID);
        ShowAssetPermission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found."));
        return modelMapper.map(permission, ShowAssetPermissionDTO.class);
    }

    public List<ShowAssetPermissionDTO> getAllPermissions() {
        List<ShowAssetPermission> all = permissionRepository.findAll();
        return all.stream()
                .map(p -> modelMapper.map(p, ShowAssetPermissionDTO.class))
                .collect(Collectors.toList());
    }

    public ShowAssetPermissionDTO updatePermission(CreateShowAssetPermissionDTO dto) {
        ShowAssetPermissionId id = new ShowAssetPermissionId(dto.getAssetTypeID(), dto.getProjectTypeID());
        ShowAssetPermission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found."));
        permission.setAllowed(dto.isAllowed());
        permission.setIsEssential(dto.isEssential());
        ShowAssetPermission updated = permissionRepository.save(permission);
        return modelMapper.map(updated, ShowAssetPermissionDTO.class);
    }

    public void deletePermission(String showTypeID, String assetTypeID) {
        ShowAssetPermissionId id = new ShowAssetPermissionId(showTypeID, assetTypeID);
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("Permission not found.");
        }
        permissionRepository.deleteById(id);
    }
}
