package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.DepartmentProject;
import com.nvsstagemanagement.nvs_stage_management.model.DepartmentProjectId;
import com.nvsstagemanagement.nvs_stage_management.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentProjectRepository extends JpaRepository<DepartmentProject, DepartmentProjectId> {
    @Query("SELECT dp.project FROM DepartmentProject dp WHERE dp.department.departmentId = :departmentId")
    List<Project> findProjectsByDepartmentId(@Param("departmentId") String departmentId);
}
