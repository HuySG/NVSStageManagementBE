package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.DepartmentShow;
import com.nvsstagemanagement.nvs_stage_management.model.DepartmentShowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentShowRepository extends JpaRepository<DepartmentShow, DepartmentShowId> {
}
