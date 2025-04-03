package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectTypeDTO;

import java.util.List;

public interface IProjectTypeService {
    List<ProjectTypeDTO> getAllProjectTypes();
}
