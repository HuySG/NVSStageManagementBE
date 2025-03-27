package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.project.DepartmentProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectDepartmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectMilestoneDepartmentDTO;

import java.util.List;

public interface IProjectService {
    List<ProjectDepartmentDTO> getAllProject();
    ProjectDepartmentDTO createProject(ProjectDepartmentDTO projectDTO);
    List<DepartmentProjectDTO> assignDepartmentToProject(String projectID, DepartmentProjectDTO departmentProjectDTO);
    List<ProjectMilestoneDepartmentDTO> getAllProjectWithMilestone();
    List<ProjectDepartmentDTO> getProjectWithUserId(String userId);
    List<ProjectDepartmentDTO> getProjectsByDepartmentId(String departmentId);
}
