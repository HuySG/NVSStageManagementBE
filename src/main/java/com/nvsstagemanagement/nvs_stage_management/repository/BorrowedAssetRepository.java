package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAsset;
import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAssetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowedAssetRepository extends JpaRepository<BorrowedAsset, String> {
}
