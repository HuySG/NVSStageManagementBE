package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.ProjectAssetPermission;
import com.nvsstagemanagement.nvs_stage_management.model.ProjectAssetPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowAssetPermissionRepository extends JpaRepository<ProjectAssetPermission, ProjectAssetPermissionId> {
}
