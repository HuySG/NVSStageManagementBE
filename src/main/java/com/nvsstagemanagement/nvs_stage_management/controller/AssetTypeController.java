package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetTypeDTO;
import com.nvsstagemanagement.nvs_stage_management.service.impl.AssetTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssetTypeById(@RequestParam String id) {
        try {
            AssetTypeDTO result = assetTypeService.getAssetTypeById(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }
    @PostMapping
    public ResponseEntity<?> createAssetType(@RequestBody AssetTypeDTO assetTypeDTO) {
        try {
            AssetTypeDTO result = assetTypeService.createAssetType(assetTypeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
    @PutMapping
    public ResponseEntity<?> updateAssetType(@RequestBody AssetTypeDTO assetTypeDTO) {
        try {
            AssetTypeDTO result = assetTypeService.updateAssetType(assetTypeDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssetType(@RequestParam String id) {
        try {
            assetTypeService.deleteAssetType(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }
}
