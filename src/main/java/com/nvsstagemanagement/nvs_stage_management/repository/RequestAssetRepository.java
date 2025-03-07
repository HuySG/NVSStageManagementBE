package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.RequestAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestAssetRepository extends JpaRepository<RequestAsset, String> {
    @Query(value = "SELECT DISTINCT ra.* " +
            "FROM RequestAsset ra " +
            "JOIN Task t ON ra.TaskID = t.TaskID " +
            "JOIN TaskUser tu ON t.TaskID = tu.TaskID " +
            "JOIN [User] u ON tu.UserID = u.ID " +
            "JOIN Department d ON u.DepartmentId = d.ID " +
            "WHERE d.ID = :departmentId " +
            "AND ra.Status = :status", nativeQuery = true)
    List<RequestAsset> findRequestsForDepartmentLeader(@Param("departmentId") String departmentId,
                                                       @Param("status") String status);
    @Query(value = "SELECT DISTINCT ra.* " +
            "FROM RequestAsset ra " +
            "JOIN Task t ON ra.TaskID = t.TaskID " +
            "JOIN TaskUser tu ON t.TaskID = tu.TaskID " +
            "JOIN [User] u ON tu.UserID = u.ID " +
            "WHERE u.ID = :userId", nativeQuery = true)
    List<RequestAsset> findByUserId(@Param("userId") String userId);
    List<RequestAsset> findByStatusIn(Collection<String> statuses);
}
