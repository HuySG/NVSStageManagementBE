package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;

@Repository
public interface BorrowedAssetRepository extends JpaRepository<BorrowedAsset, String> {
    @Query(value = "SELECT COALESCE(SUM(b.Quantity), 0) " +
            "FROM BorrowedAsset b " +
            "WHERE b.AssetID = :assetId " +
            "AND (:startTime < b.EndTime) " +
            "AND (:endTime > b.BorrowTime)", nativeQuery = true)
    int getOverlappingBorrowedQuantity(@Param("assetId") String assetId,
                                       @Param("startTime") Instant startTime,
                                       @Param("endTime") Instant endTime);
    @Query(value = "SELECT COUNT(*) FROM BorrowedAsset b " +
            "JOIN Asset a ON b.AssetID = a.AssetID " +
            "WHERE a.AssetTypeID = :assetTypeID " +
            "AND (:startTime < b.EndTime) " +
            "AND (:endTime > b.BorrowTime)", nativeQuery = true)
    int countOverlapping(@Param("assetTypeID") String assetTypeID,
                         @Param("startTime") Instant startTime,
                         @Param("endTime") Instant endTime);
}
