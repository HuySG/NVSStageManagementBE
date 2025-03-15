package com.nvsstagemanagement.nvs_stage_management.dto.asset;

import com.nvsstagemanagement.nvs_stage_management.dto.category.CategoryDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssetTypeDTO {
    private String id;
    private String name;
    private List<CategoryDTO> categories;
}
