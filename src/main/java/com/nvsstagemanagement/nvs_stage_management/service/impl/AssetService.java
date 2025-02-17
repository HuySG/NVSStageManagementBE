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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService implements IAssetService {
    private final AssetRepository assetRepository;
    private final CategoryRepository categoryRepository;
    private final AssetTypeRepository assetTypeRepository;
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
        Asset createAsset = modelMapper.map(createAssetDTO, Asset.class);
        Asset savedAsset = assetRepository.save(createAsset);
        return modelMapper.map(savedAsset, AssetDTO.class);
    }

    @Override
    public AssetDTO updateAsset(UpdateAssetDTO updateAssetDTO) {
        Asset asset = assetRepository.findById(updateAssetDTO.getAssetID())
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        Category existingCategory = asset.getCategory();
        AssetType existingAssetType = asset.getAssetType();
        asset.setAssetName(updateAssetDTO.getAssetName());
        asset.setModel(updateAssetDTO.getModel());
        asset.setCode(updateAssetDTO.getCode());
        asset.setDescription(updateAssetDTO.getDescription());
        asset.setPrice(updateAssetDTO.getPrice());
        asset.setBuyDate(updateAssetDTO.getBuyDate());
        asset.setStatus(updateAssetDTO.getStatus());
        asset.setLocation(updateAssetDTO.getLocation());
        asset.setCreatedBy(updateAssetDTO.getCreatedBy());
        asset.setQuantity(updateAssetDTO.getQuantity());
        asset.setImage(updateAssetDTO.getImage());
        asset.setCategory(existingCategory);
        asset.setAssetType(existingAssetType);
        Asset updatedAsset = assetRepository.save(asset);
        return modelMapper.map(updatedAsset, AssetDTO.class);
    }
}
