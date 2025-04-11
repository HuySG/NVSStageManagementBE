package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.ReturnedAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnedAssetRepository extends JpaRepository<ReturnedAsset, String> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM ReturnedAsset r " +
            "WHERE r.assetID = :assetID AND r.taskID = :taskID")
    boolean existsByAssetIDAndTaskID(@Param("assetID") String assetID,
                                     @Param("taskID") String taskID);
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM ReturnedAsset r " +
            "WHERE r.assetID.assetID = :assetID")
    boolean existsReturnedAssetByAssetID(@Param("assetID") String assetID);
    @Query("SELECT COUNT(r) FROM ReturnedAsset r WHERE r.taskID.endDate IS NOT NULL AND r.returnTime > r.taskID.endDate")
    long countLateReturnedAssets();
    @Query("SELECT r FROM ReturnedAsset r WHERE r.returnTime > r.taskID.endDate")
    List<ReturnedAsset> findLateReturnedAssets();

}
