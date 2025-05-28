package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {
    List<Asset> findByAssetNameContainingIgnoreCase(String name);
    List<Asset> findByAssetType_AssetTypeID(String assetTypeID);
    List<Asset> findByCategory_CategoryID(String categoryID);
    @Query("SELECT a FROM Asset a WHERE a.category.categoryID = :categoryID AND a.status = 'AVAILABLE'")
    List<Asset> findAvailableAssetsByCategory(@Param("categoryID") String categoryID);
    @Query("SELECT a FROM Asset a WHERE a.status = 'Need Maintenance'")
    List<Asset> findAssetsNeedingMaintenance();

}
