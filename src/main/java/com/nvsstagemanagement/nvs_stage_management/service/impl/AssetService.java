package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.CategoryDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.AssetTypeDTO;
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
        return assetRepository.findAll().stream()
                .map(asset -> modelMapper.map(asset, AssetDTO.class))
                .collect(Collectors.toList());
    }
}
