package com.nvsstagemanagement.nvs_stage_management.controller;


import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IBorrowedAssetService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borrowed-assets")
@RequiredArgsConstructor
public class BorrowedAssetController {


    private final IBorrowedAssetService borrowedAssetService;

    @PostMapping("/create")
    public ResponseEntity<BorrowedAssetDTO> createBorrowedAsset(@RequestBody BorrowedAssetDTO borrowedAssetDTO) {
        BorrowedAssetDTO savedAsset = borrowedAssetService.createBorrowedAsset(borrowedAssetDTO);
        return ResponseEntity.ok(savedAsset);
    }

    @GetMapping
    public ResponseEntity<List<BorrowedAssetDTO>> getAllBorrowedAssets() {
        return ResponseEntity.ok(borrowedAssetService.getAllBorrowedAssets());
    }

    @GetMapping("/{borrowedId}")
    public ResponseEntity<BorrowedAssetDTO> getBorrowedAssetById(@PathVariable String borrowedId) {
        return borrowedAssetService.getBorrowedAssetById(borrowedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{borrowedId}")
    public ResponseEntity<Void> deleteBorrowedAsset(@PathVariable String borrowedId) {
        borrowedAssetService.deleteBorrowedAsset(borrowedId);
        return ResponseEntity.noContent().build();
    }

}
