package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.asset.CreateAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.asset.UpdateAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/asset")
@RequiredArgsConstructor
public class AssetController {
    private final IAssetService assetService;

    @GetMapping
    public List<AssetDTO> getAllAssets() {
        return assetService.getAllAsset();
    }
    @GetMapping("/search")
    public List<AssetDTO> getAssetByName(@RequestParam String name) {
        return assetService.getAssetByName(name);
    }
    @PostMapping
    public ResponseEntity<AssetDTO> createAsset(@RequestBody CreateAssetDTO createAssetDTO) {
        AssetDTO createdAsset = assetService.createAsset(createAssetDTO);
        return new ResponseEntity<>(createdAsset, HttpStatus.CREATED);
    }
    @PutMapping
    public ResponseEntity<AssetDTO> updateAsset( @RequestBody UpdateAssetDTO updateAssetDTO) {
        AssetDTO updatedAsset = assetService.updateAsset(updateAssetDTO);
        return ResponseEntity.ok(updatedAsset);
    }
    @GetMapping("/asset-type")
    public ResponseEntity<List<AssetDTO>> getByAssetTypeID(@RequestParam String ID) {
        List<AssetDTO> assetDTOs = assetService.getByAssetTypeID(ID);
        return ResponseEntity.ok(assetDTOs);
    }
    @GetMapping("/category")
    public ResponseEntity<List<AssetDTO>> getByCategoryID(@RequestParam String ID) {
        List<AssetDTO> assetDTOs = assetService.getByCategoryID(ID);
        return ResponseEntity.ok(assetDTOs);
    }
}
