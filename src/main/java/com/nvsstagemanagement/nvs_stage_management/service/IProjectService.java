package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.project.CreateProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectDTO;

import java.util.List;

public interface IProjectService {
    List<ProjectDTO> getAllProject();
    ProjectDTO createProject(ProjectDTO projectDTO);
}
