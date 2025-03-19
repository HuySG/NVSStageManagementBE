package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetTypeDTO;
import com.nvsstagemanagement.nvs_stage_management.model.AssetType;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetTypeRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IAssetTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetTypeService implements IAssetTypeService {
    private final AssetTypeRepository assetTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AssetTypeDTO> getAllAssetTypesWithCategories() {
        List<AssetType> assetTypes = assetTypeRepository.findAll();
        return assetTypes.stream()
                .map(assetType -> modelMapper.map(assetType, AssetTypeDTO.class))
                .collect(Collectors.toList());
    }
}
