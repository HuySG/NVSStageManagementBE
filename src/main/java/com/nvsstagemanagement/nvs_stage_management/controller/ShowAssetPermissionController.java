package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.showAsset.CreateShowAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.showAsset.ShowAssetPermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.service.impl.ShowAssetPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project-asset-permissions")
@RequiredArgsConstructor
public class ShowAssetPermissionController {

    private final ShowAssetPermissionService permissionService;


    @PostMapping
    public ResponseEntity<ShowAssetPermissionDTO> create(@RequestBody CreateShowAssetPermissionDTO dto) {
        ShowAssetPermissionDTO response = permissionService.createPermission(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/showTypeID/assetTypeID")
    public ResponseEntity<ShowAssetPermissionDTO> getPermission(
            @RequestParam String showTypeID,
            @RequestParam String assetTypeID) {
        ShowAssetPermissionDTO response = permissionService.getPermission(showTypeID, assetTypeID);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ShowAssetPermissionDTO>> getAll() {
        List<ShowAssetPermissionDTO> list = permissionService.getAllPermissions();
        return ResponseEntity.ok(list);
    }

    @PutMapping
    public ResponseEntity<ShowAssetPermissionDTO> update(@RequestBody CreateShowAssetPermissionDTO dto) {
        ShowAssetPermissionDTO response = permissionService.updatePermission(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/showTypeID/assetTypeID")
    public ResponseEntity<Void> delete(@RequestParam String showTypeID, @RequestParam String assetTypeID) {
        permissionService.deletePermission(showTypeID, assetTypeID);
        return ResponseEntity.noContent().build();
    }
}
