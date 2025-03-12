package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.ShowAssetPermission;
import com.nvsstagemanagement.nvs_stage_management.model.ShowAssetPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowAssetPermissionRepository extends JpaRepository<ShowAssetPermission, ShowAssetPermissionId> {
}
