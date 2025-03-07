package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {
    List<Asset> findByAssetNameContainingIgnoreCase(String name);
    List<Asset> findByAssetType_AssetTypeID(String assetTypeID);
    List<Asset> findByCategory_CategoryID(String categoryID);
}
