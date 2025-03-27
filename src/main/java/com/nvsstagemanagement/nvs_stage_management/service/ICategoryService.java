package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.category.CategoryDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.category.CreateCategoryDTO;

import java.util.List;

public interface ICategoryService {

    CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO);
    CategoryDTO updateCategory(CategoryDTO categoryDTO);
    void deleteCategory(String categoryID);

}
