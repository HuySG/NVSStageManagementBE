package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.tasks")
    List<Project> findAllWithTasks();
    @Query(value = "SELECT DISTINCT p.* FROM \"User\" u " +
            "JOIN Department d ON u.DepartmentId = d.ID " +
            "JOIN DepartmentProject dp ON dp.DepartmentId = d.ID " +
            "JOIN Project p ON dp.ProjectId = p.ProjectID " +
            "WHERE u.ID = :userId", nativeQuery = true)
    List<Project> findProjectsByUserId(@Param("userId") String userId);
}
