package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetTypeDTO;
import com.nvsstagemanagement.nvs_stage_management.service.impl.AssetTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asset-types")
@RequiredArgsConstructor
public class AssetTypeController {
    private final AssetTypeService assetTypeService;

    @GetMapping
    public ResponseEntity<List<AssetTypeDTO>> getAssetTypesAndCategories() {
        List<AssetTypeDTO> assetTypes = assetTypeService.getAllAssetTypesWithCategories();
        return ResponseEntity.ok(assetTypes);
    }
}
