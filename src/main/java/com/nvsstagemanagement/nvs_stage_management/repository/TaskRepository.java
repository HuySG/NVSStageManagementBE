package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.TaskEnum;
import com.nvsstagemanagement.nvs_stage_management.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    @Query("SELECT DISTINCT t FROM Task t " +
            "LEFT JOIN FETCH t.taskUsers tu " +
            "LEFT JOIN FETCH tu.user " +
            "LEFT JOIN FETCH User u ON t.assignee = u.id " +
            "WHERE t.milestone.milestoneID = :milestoneID")
    List<Task> findTasksWithUsersByMilestoneId(String milestoneID);
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.taskUsers tu LEFT JOIN FETCH tu.user WHERE t.taskID = :taskId")
    Optional<Task> findTaskWithUsersByTaskId(String taskId);

    @Query("""
        SELECT DISTINCT t
        FROM Task t
        LEFT JOIN t.taskUsers tu
        WHERE t.assignee = :userId
           OR tu.user.id = :userId
    """)
    List<Task> findTasksByUserId(@Param("userId") String userId);
    List<Task> findByStatus(TaskEnum status);
    @Query("SELECT t FROM Task t WHERE t.milestone.project.projectID = :projectId")
    List<Task> findByProjectId(String projectId);
    @Query("SELECT t FROM Task t WHERE t.assigneeUser.department.departmentId = :departmentId")
    List<Task> findTasksByDepartmentId(@Param("departmentId") String departmentId);
    @Query("""
    SELECT t FROM Task t
    WHERE 
        t.milestone IS NULL
        AND t.tag = 'Prepare asset'
        AND t.taskID IN (
            SELECT root.dependsOnTaskID
            FROM Task root
            WHERE root.milestone.project.projectID = :projectId
              AND root.dependsOnTaskID IS NOT NULL
        )
    """)
    List<Task> findPrepareTasksUsedByProject(@Param("projectId") String projectId);
    Optional<Task> findFirstByDependsOnTaskID(String prepareTaskId);
    @Query("""
      SELECT t
      FROM Task t
      JOIN t.milestone m
      WHERE t.tag = 'Prepare asset'
        AND t.createBy IN :creatorIds
    """)
    List<Task> findPrepareTasksByCreators(@Param("creatorIds") List<String> creatorIds);

}