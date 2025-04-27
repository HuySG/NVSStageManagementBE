package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetsOverviewDTO;

import java.util.List;
import java.util.Optional;

public interface IBorrowedAssetService {
    BorrowedAssetDTO createBorrowedAsset(BorrowedAssetDTO borrowedAssetDTO);

    List<BorrowedAssetDTO> getAllBorrowedAssets();

    Optional<BorrowedAssetDTO> getBorrowedAssetById(String borrowedId);

    void deleteBorrowedAsset(String borrowedId);
    BorrowedAssetsOverviewDTO getBorrowedAssetsOverview();
}
