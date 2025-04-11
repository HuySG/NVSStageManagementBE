package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.service.impl.AdminStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticController {

    private final AdminStatisticService adminStatisticService;

    @GetMapping
    public Map<String, Object> getStatistics() {
        return adminStatisticService.getAdminStatistics();
    }
    @GetMapping("/assets")
    public List<AssetDTO> getAllAssets() {
        return adminStatisticService.getAllAssets();
    }

    @GetMapping("/tasks")
    public List<TaskDTO> getAllTasks() {
        return adminStatisticService.getAllTasks();
    }

    @GetMapping("/requests")
    public List<RequestAssetDTO> getAllRequests() {
        return adminStatisticService.getAllRequests();
    }

    @GetMapping("/borrowed-assets")
    public List<BorrowedAssetDTO> getAllBorrowedAssets() {
        return adminStatisticService.getAllBorrowedAssets();
    }

    @GetMapping("/returned-assets")
    public List<ReturnedAssetDTO> getAllReturnedAssets() {
        return adminStatisticService.getAllReturnedAssets();
    }

    @GetMapping("/maintenance-needed")
    public List<AssetDTO> getAssetsNeedingMaintenance() {
        return adminStatisticService.getAssetsNeedingMaintenance();
    }

    @GetMapping("/late-returns")
    public List<ReturnedAssetDTO> getLateReturnedAssets() {
        return adminStatisticService.getLateReturnedAssets();
    }
}
