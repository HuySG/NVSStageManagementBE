package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetRepository;
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
    public AssetDTO createAsset(AssetDTO assetDTO) {
        Asset createAsset = modelMapper.map(assetDTO, Asset.class);
        Asset savedAsset = assetRepository.save(createAsset);
        return modelMapper.map(savedAsset, AssetDTO.class);
    }
}
