package com.nvsstagemanagement.nvs_stage_management.service.impl;
import com.nvsstagemanagement.nvs_stage_management.dto.category.CategoryDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.category.CreateCategoryDTO;
import com.nvsstagemanagement.nvs_stage_management.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {


    @Override
    public CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO) {
        return null;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        return null;
    }

    @Override
    public void deleteCategory(String categoryID) {

    }
}
