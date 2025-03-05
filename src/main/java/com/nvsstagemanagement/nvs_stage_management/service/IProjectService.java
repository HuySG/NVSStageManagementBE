package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.project.CreateProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.DepartmentProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectTaskDTO;

import java.util.List;

public interface IProjectService {
    List<ProjectDTO> getAllProject();
    ProjectDTO createProject(ProjectDTO projectDTO);
    DepartmentProjectDTO assignDepartmentToProject(DepartmentProjectDTO departmentProjectDTO);
    List<ProjectTaskDTO> getAllProjectsWithTasks();
    List<ProjectDTO> getProjectWithUserId(String userId);
}
