package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.projectAsset.CreateProjectAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.projectAsset.ProjectAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.service.impl.ProjectAssetPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project-asset-permissions")
@RequiredArgsConstructor
public class ProjectAssetPermissionController {

    private final ProjectAssetPermissionService permissionService;


    @PostMapping
    public ResponseEntity<ProjectAssetPermissionDTO> create(@RequestBody CreateProjectAssetPermissionDTO dto) {
        ProjectAssetPermissionDTO response = permissionService.createPermission(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/showTypeID/assetTypeID")
    public ResponseEntity<ProjectAssetPermissionDTO> getPermission(
            @RequestParam String showTypeID,
            @RequestParam String assetTypeID) {
        ProjectAssetPermissionDTO response = permissionService.getPermission(showTypeID, assetTypeID);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectAssetPermissionDTO>> getAll() {
        List<ProjectAssetPermissionDTO> list = permissionService.getAllPermissions();
        return ResponseEntity.ok(list);
    }

    @PutMapping
    public ResponseEntity<ProjectAssetPermissionDTO> update(@RequestBody CreateProjectAssetPermissionDTO dto) {
        ProjectAssetPermissionDTO response = permissionService.updatePermission(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/showTypeID/assetTypeID")
    public ResponseEntity<Void> delete(@RequestParam String showTypeID, @RequestParam String assetTypeID) {
        permissionService.deletePermission(showTypeID, assetTypeID);
        return ResponseEntity.noContent().build();
    }
}
