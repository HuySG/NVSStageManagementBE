package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.IAdminStatisticService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStatisticService implements IAdminStatisticService {

    private final AssetRepository assetRepository;
    private final TaskRepository taskRepository;
    private final RequestAssetRepository requestAssetRepository;
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final ReturnedAssetRepository returnedAssetRepository;
    private final AssetUsageHistoryRepository assetUsageHistoryRepository;
    private final ModelMapper modelMapper;


    @Override
    public Map<String, Object> getAdminStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalAssets = assetRepository.count();
        stats.put("totalAssets", totalAssets);

        long totalTasks = taskRepository.count();
        stats.put("totalTasks", totalTasks);

        long totalRequests = requestAssetRepository.count();
        stats.put("totalRequests", totalRequests);

        long totalBorrowed = borrowedAssetRepository.count();
        stats.put("totalBorrowedAssets", totalBorrowed);

        long totalReturned = returnedAssetRepository.count();
        stats.put("totalReturnedAssets", totalReturned);

        long totalLateReturns = returnedAssetRepository.countLateReturnedAssets();
        stats.put("totalLateReturns", totalLateReturns);

        long totalMaintenanceNeeded = assetUsageHistoryRepository.countAssetsNeedingMaintenance();
        stats.put("totalMaintenanceNeeded", totalMaintenanceNeeded);

        return stats;
    }
    @Override
    public List<AssetDTO> getAllAssets() {
        List<Asset> assets = assetRepository.findAll();
        return assets.stream()
                .map(asset -> modelMapper.map(asset, AssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestAssetDTO> getAllRequests() {
        List<RequestAsset> requests = requestAssetRepository.findAll();
        return requests.stream()
                .map(request -> modelMapper.map(request, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowedAssetDTO> getAllBorrowedAssets() {
        List<BorrowedAsset> borrowedAssets = borrowedAssetRepository.findAll();
        return borrowedAssets.stream()
                .map(borrowed -> modelMapper.map(borrowed, BorrowedAssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReturnedAssetDTO> getAllReturnedAssets() {
        List<ReturnedAsset> returnedAssets = returnedAssetRepository.findAll();
        return returnedAssets.stream()
                .map(returned -> modelMapper.map(returned, ReturnedAssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetDTO> getAssetsNeedingMaintenance() {
        List<Asset> assets = assetRepository.findAssetsNeedingMaintenance();
        return assets.stream()
                .map(asset -> modelMapper.map(asset, AssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReturnedAssetDTO> getLateReturnedAssets() {
        List<ReturnedAsset> lateReturns = returnedAssetRepository.findLateReturnedAssets();
        return lateReturns.stream()
                .map(returned -> modelMapper.map(returned, ReturnedAssetDTO.class))
                .collect(Collectors.toList());
    }
}
