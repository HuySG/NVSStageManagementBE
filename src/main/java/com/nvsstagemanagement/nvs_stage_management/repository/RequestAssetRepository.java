package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import com.nvsstagemanagement.nvs_stage_management.model.RequestAsset;
import com.nvsstagemanagement.nvs_stage_management.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestAssetRepository extends JpaRepository<RequestAsset, String> {
    @Query("SELECT r FROM RequestAsset r JOIN User u ON r.createBy = u.id " +
            "WHERE u.department.departmentId = :departmentId")
    List<RequestAsset> findRequestsForDepartmentLeader(@Param("departmentId") String departmentId);
    @Query(value = "SELECT DISTINCT ra.* " +
            "FROM RequestAsset ra " +
            "JOIN Task t ON ra.TaskID = t.TaskID " +
            "JOIN TaskUser tu ON t.TaskID = tu.TaskID " +
            "JOIN [User] u ON tu.UserID = u.ID " +
            "WHERE u.ID = :userId", nativeQuery = true)
    List<RequestAsset> findByUserId(@Param("userId") String userId);
    List<RequestAsset> findByStatusIn(Collection<String> statuses);
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM RequestAsset r " +
            "WHERE r.task.taskID = :taskId " +
            "AND r.status NOT IN :excludedStatuses")
    boolean existsByTaskIdAndStatusNotIn(@Param("taskId") String taskId,
                                         @Param("excludedStatuses") List<String> excludedStatuses);
    boolean existsByTaskAndStatusNotIn(Task task, List<String> statuses);
    boolean existsByTaskAndAssetAndStatusNotIn(Task task, Asset asset, List<String> statuses);


}
