package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import com.nvsstagemanagement.nvs_stage_management.model.AssetUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetUsageHistoryRepository extends JpaRepository<AssetUsageHistory, String> {
    List<AssetUsageHistory> findByAssetOrderByStartDateDesc(Asset asset);
    Optional<AssetUsageHistory> findByAsset_AssetIDAndProject_ProjectID(String assetID, String projectID);

}
