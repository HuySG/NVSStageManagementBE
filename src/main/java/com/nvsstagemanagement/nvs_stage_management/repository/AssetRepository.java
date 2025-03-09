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
    int countByAssetType_AssetTypeID(String assetTypeID);
    @Query(value = "SELECT a.* FROM Asset a " +
            "WHERE a.AssetTypeID = :assetTypeID " +
            "AND a.AssetID NOT IN (" +
            "   SELECT b.AssetID FROM BorrowedAsset b " +
            "   WHERE (:startTime < b.EndTime) AND (:endTime > b.BorrowTime)" +
            ")", nativeQuery = true)
    List<Asset> findAvailableAssets(@Param("assetTypeID") String assetTypeID,
                                    @Param("startTime") Instant startTime,
                                    @Param("endTime") Instant endTime);
}
