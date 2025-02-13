package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
