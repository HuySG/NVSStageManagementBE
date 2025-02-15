package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.ProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Project;

import java.util.List;

public interface IProjectService {
    List<ProjectDTO> getAllProject();
}
