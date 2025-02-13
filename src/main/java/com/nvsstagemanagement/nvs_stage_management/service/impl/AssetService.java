package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.CategoryDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.AssetTypeDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import com.nvsstagemanagement.nvs_stage_management.model.Category;
import com.nvsstagemanagement.nvs_stage_management.model.AssetTypy;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService implements IAssetService {
    private final AssetRepository assetRepository;

    @Override
    public List<AssetDTO> getAllAsset() {
        return assetRepository.findAll().stream().map(a -> {
            AssetDTO dto = new AssetDTO();
            dto.setAssetID(a.getAssetID());
            dto.setAssetName(a.getAssetName());
            dto.setModel(a.getModel());
            dto.setCode(a.getCode());
            dto.setDescription(a.getDescription());
            dto.setPrice(a.getPrice());
            dto.setBuyDate(a.getBuyDate());
            dto.setStatus(a.getStatus());
            dto.setLocation(a.getLocation());
            dto.setCreatedBy(a.getCreatedBy());
            dto.setQuantity(a.getQuantity());
            dto.setImage(a.getImage());

            if (a.getCategoryID() != null) {
                Category category = a.getCategoryID();
                CategoryDTO categoryDTO = new CategoryDTO();
                categoryDTO.setCategoryID(category.getCateforyID());
                categoryDTO.setName(category.getName());
                dto.setCategory(categoryDTO);
            }

            if (a.getAssetType() != null) {
                AssetTypy assetType = a.getAssetType();
                AssetTypeDTO assetTypeDTO = new AssetTypeDTO();
                assetTypeDTO.setAssetTypeID(assetType.getId());
                assetTypeDTO.setName(assetType.getName());
                dto.setAssetType(assetTypeDTO);
            }

            return dto;
        }).collect(Collectors.toList());
    }
}
