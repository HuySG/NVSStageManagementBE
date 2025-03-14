package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.project.DepartmentProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectMilestoneDTO;

import java.util.List;

public interface IProjectService {
    List<ProjectDTO> getAllProject();
    ProjectDTO createProject(ProjectDTO projectDTO);
    DepartmentProjectDTO assignDepartmentToProject(DepartmentProjectDTO departmentProjectDTO);
    List<ProjectMilestoneDTO> getAllProjectWithMilestone();
    List<ProjectDTO> getProjectWithUserId(String userId);
}
