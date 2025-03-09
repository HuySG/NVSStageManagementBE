package com.nvsstagemanagement.nvs_stage_management.repository;


import com.nvsstagemanagement.nvs_stage_management.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
