package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.asset.CreateAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.asset.UpdateAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import com.nvsstagemanagement.nvs_stage_management.model.AssetType;
import com.nvsstagemanagement.nvs_stage_management.model.Category;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetTypeRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.CategoryRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IAssetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService implements IAssetService {
    private final AssetRepository assetRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AssetDTO> getAllAsset() {
        List<Asset> assets = assetRepository.findAll();

         return assets.stream()
                .map(asset -> modelMapper.map(asset, AssetDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<AssetDTO> getAssetByName(String name) {
        return assetRepository.findByAssetNameContainingIgnoreCase(name).stream()
                .map(asset -> modelMapper.map(asset, AssetDTO.class)).toList();
    }
    @Override
    public AssetDTO createAsset(CreateAssetDTO createAssetDTO) {

        if (createAssetDTO == null) {
            throw new IllegalArgumentException("Asset data is required.");
        }
        if (createAssetDTO.getAssetName() == null || createAssetDTO.getAssetName().trim().isEmpty()) {
            throw new IllegalArgumentException("Asset name is required.");
        }
        if (createAssetDTO.getAssetTypeID() == null || createAssetDTO.getAssetTypeID().trim().isEmpty()) {
            throw new IllegalArgumentException("Asset type ID is required.");
        }
        if (createAssetDTO.getCategoryID() == null || createAssetDTO.getCategoryID().trim().isEmpty()) {
            throw new IllegalArgumentException("Category ID is required.");
        }

        if (createAssetDTO.getAssetID() == null || createAssetDTO.getAssetID().trim().isEmpty()) {
            createAssetDTO.setAssetID(UUID.randomUUID().toString());
        }
        if (createAssetDTO.getLocationId() == null || createAssetDTO.getLocationId().trim().isEmpty()) {
            createAssetDTO.setLocationId("STORAGE");
        }
        Asset createAsset = modelMapper.map(createAssetDTO, Asset.class);
        Asset savedAsset = assetRepository.save(createAsset);
        return modelMapper.map(savedAsset, AssetDTO.class);
    }

    @Override
    public AssetDTO updateAsset(UpdateAssetDTO updateAssetDTO) {
        Asset asset = assetRepository.findById(updateAssetDTO.getAssetID())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        Category category = categoryRepository.findById(updateAssetDTO.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        asset.setCategory(category);
        AssetType assetType = assetTypeRepository.findById(updateAssetDTO.getAssetTypeID())
                .orElseThrow(() -> new RuntimeException("AssetType not found"));
        asset.setAssetType(assetType);

        asset.setAssetName(updateAssetDTO.getAssetName());
        asset.setModel(updateAssetDTO.getModel());
        asset.setCode(updateAssetDTO.getCode());
        asset.setDescription(updateAssetDTO.getDescription());
        asset.setPrice(updateAssetDTO.getPrice());
        asset.setBuyDate(updateAssetDTO.getBuyDate());
        asset.setStatus(updateAssetDTO.getStatus());
        asset.setCreatedBy(updateAssetDTO.getCreatedBy());
        asset.setImage(updateAssetDTO.getImage());

        Asset updatedAsset = assetRepository.save(asset);
        return modelMapper.map(updatedAsset, UpdateAssetDTO.class);
    }

    @Override
    public List<AssetDTO> getByAssetTypeID(String assetTypeID) {
        List<Asset> assets = assetRepository.findByAssetType_AssetTypeID(assetTypeID);
        return assets.stream()
                .map(asset -> modelMapper.map(asset, AssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetDTO> getByCategoryID(String categoryID) {
        List<Asset> assets = assetRepository.findByCategory_CategoryID(categoryID);
        return assets.stream()
                .map(asset -> modelMapper.map(asset, AssetDTO.class))
                .collect(Collectors.toList());
    }


}
