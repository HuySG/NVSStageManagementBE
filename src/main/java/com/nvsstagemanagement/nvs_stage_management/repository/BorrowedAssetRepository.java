package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowedAssetRepository extends JpaRepository<BorrowedAsset, String> {
    List<BorrowedAsset> findByTask_TaskID(String taskId);
    @Query("SELECT COUNT(b) > 0 FROM BorrowedAsset b " +
            "WHERE b.asset.assetID = :assetID " +
            "AND b.endTime IS NULL")
    boolean existsActiveBorrow(@Param("assetID") String assetID);

    List<BorrowedAsset> findAllByStatus(String status);
    Optional<BorrowedAsset> findByAsset_AssetIDAndTask_TaskIDAndStatus(String assetID, String taskID, String status);
    @Query("SELECT COUNT(b) > 0 FROM BorrowedAsset b " +
            "WHERE b.asset.assetID = :assetId " +
            "AND ((b.startTime <= :endTime AND b.endTime >= :startTime))")
    boolean existsAssetConflict(@Param("assetId") String assetId,
                                @Param("startTime") Instant startTime,
                                @Param("endTime") Instant endTime);
    @Query("SELECT MIN(b.endTime) FROM BorrowedAsset b " +
            "WHERE b.asset.assetID = :assetId AND b.endTime > :afterTime")
    Instant findNextAvailableTime(@Param("assetId") String assetId,
                                  @Param("afterTime") Instant afterTime);
    Optional<BorrowedAsset> findByAsset_AssetIDAndTask_TaskID(String assetId, String taskId);
    List<BorrowedAsset> findByTask_AssigneeAndStatus(String assignee, String status);
    List<BorrowedAsset> findByStatusIn(List<String> statuses);
}
