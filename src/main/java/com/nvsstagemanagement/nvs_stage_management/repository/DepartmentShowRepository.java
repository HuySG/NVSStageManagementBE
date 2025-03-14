package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.DepartmentProject;
import com.nvsstagemanagement.nvs_stage_management.model.DepartmentProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentShowRepository extends JpaRepository<DepartmentProject, DepartmentProjectId> {
}
