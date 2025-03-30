package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.allocation.AssetUsageHistoryDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asset")
@RequiredArgsConstructor
public class AssetUsageHistoryController {

    private final IAllocationService allocationService;

    @GetMapping("/{assetId}/usage-history")
    public ResponseEntity<List<AssetUsageHistoryDTO>> getUsageHistory(@PathVariable String assetId) {
        List<AssetUsageHistoryDTO> historyDTOs = allocationService.getUsageHistoryByAsset(assetId);
        return ResponseEntity.ok(historyDTOs);
    }
}
