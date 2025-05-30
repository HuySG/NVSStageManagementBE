package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.milestones m " +
            "LEFT JOIN FETCH m.tasks")
    List<Project> findAllWithMilestonesAndTasks();
    @Query(value = "SELECT DISTINCT p.* FROM \"User\" u " +
            "JOIN Department d ON u.DepartmentId = d.DepartmentID " +
            "JOIN DepartmentProject dp ON dp.DepartmentId = d.DepartmentID " +
            "JOIN Project p ON dp.ProjectId = p.ProjectID " +
            "WHERE u.ID = :userId", nativeQuery = true)
    List<Project> findShowByUserId(@Param("userId") String userId);
    @Query(value = "SELECT DISTINCT p.* " +
            "FROM Project p " +
            "LEFT JOIN Milestone m ON m.ProjectID = p.ProjectID " +
            "LEFT JOIN DepartmentProject dp ON dp.ProjectID = p.ProjectID " +
            "LEFT JOIN Department d ON d.DepartmentID = dp.DepartmentID " +
            "WHERE p.ProjectID = :projectId", nativeQuery = true)
    Optional<Project> findProjectWithMilestonesAndDepartmentsById(@Param("projectId") String projectId);

    @Query("""
    SELECT p 
    FROM Project p
    JOIN p.milestones m
    WHERE m.milestoneID = :milestoneId
    """)
    Optional<Project> findProjectByMilestoneId(@Param("milestoneId") String milestoneId);

}
