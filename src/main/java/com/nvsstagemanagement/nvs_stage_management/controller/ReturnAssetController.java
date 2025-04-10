package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnAssetRequestDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.model.ReturnedAsset;
import com.nvsstagemanagement.nvs_stage_management.repository.ReturnedAssetRepository;

import com.nvsstagemanagement.nvs_stage_management.service.IReturnAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnAssetController {

    private final IReturnAssetService returnAssetService;
    private final ReturnedAssetRepository returnedAssetRepository;

    @PostMapping
    public ResponseEntity<String> returnAsset(@RequestBody ReturnAssetRequestDTO dto) {
        returnAssetService.returnAsset(dto);
        return ResponseEntity.ok("Asset returned successfully!");
    }

    @GetMapping
    public ResponseEntity<List<ReturnedAssetDTO>> getReturnedAssets() {
        List<ReturnedAssetDTO> dtos = returnAssetService.getAllReturnedAssets();
        return ResponseEntity.ok(dtos);
    }

}
