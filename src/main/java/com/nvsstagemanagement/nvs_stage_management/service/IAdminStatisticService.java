package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;

import java.util.List;
import java.util.Map;

public interface IAdminStatisticService {
    Map<String, Object> getAdminStatistics();
    List<AssetDTO> getAllAssets();
    List<TaskDTO> getAllTasks();
    List<RequestAssetDTO> getAllRequests();
    List<BorrowedAssetDTO> getAllBorrowedAssets();
    List<ReturnedAssetDTO> getAllReturnedAssets();
    List<AssetDTO> getAssetsNeedingMaintenance();
    List<ReturnedAssetDTO> getLateReturnedAssets();
}
