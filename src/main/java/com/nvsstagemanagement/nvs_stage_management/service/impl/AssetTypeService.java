package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetTypeDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.category.CategoryDTO;
import com.nvsstagemanagement.nvs_stage_management.model.AssetType;
import com.nvsstagemanagement.nvs_stage_management.model.Category;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetTypeRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.CategoryRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IAssetTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetTypeService implements IAssetTypeService {
    private final AssetTypeRepository assetTypeRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AssetTypeDTO> getAllAssetTypesWithCategories() {
        List<AssetType> assetTypes = assetTypeRepository.findAll();
        return assetTypes.stream()
                .map(assetType -> modelMapper.map(assetType, AssetTypeDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AssetTypeDTO getAssetTypeById(String id) {
        AssetType assetType = assetTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AssetType not found with ID: " + id));
        return modelMapper.map(assetType, AssetTypeDTO.class);
    }

    @Override
    public AssetTypeDTO createAssetType(AssetTypeDTO assetTypeDTO) {
        AssetType assetType = modelMapper.map(assetTypeDTO, AssetType.class);
        assetType.setAssetTypeID(UUID.randomUUID().toString()); // Tạo ID tự động
        AssetType savedAssetType = assetTypeRepository.save(assetType);
        return modelMapper.map(savedAssetType, AssetTypeDTO.class);
    }


    @Override
    public AssetTypeDTO updateAssetType( AssetTypeDTO assetTypeDTO) {
        AssetType existingAssetType = assetTypeRepository.findById(assetTypeDTO.getId())
                .orElseThrow(() -> new RuntimeException("AssetType not found with ID: " + assetTypeDTO.getId()));

        existingAssetType.setName(assetTypeDTO.getName());


        if (assetTypeDTO.getCategories() != null && !assetTypeDTO.getCategories().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(
                    assetTypeDTO.getCategories().stream().map(CategoryDTO::getCategoryID).collect(Collectors.toList())
            );
            existingAssetType.setCategories(categories);
        }

        AssetType updatedAssetType = assetTypeRepository.save(existingAssetType);
        return modelMapper.map(updatedAssetType, AssetTypeDTO.class);
    }

    @Override
    public void deleteAssetType(String id) {
        AssetType existingAssetType = assetTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AssetType not found with ID: " + id));
        assetTypeRepository.delete(existingAssetType);
    }
}
