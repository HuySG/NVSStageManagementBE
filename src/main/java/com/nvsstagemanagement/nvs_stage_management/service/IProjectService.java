package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.project.DepartmentProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectMilestoneDTO;

import java.util.List;

public interface IProjectService {
    List<ProjectDTO> getAllProject();
    ProjectDTO createProject(ProjectDTO projectDTO);
    List<DepartmentProjectDTO> assignDepartmentToProject(String projectID, DepartmentProjectDTO departmentProjectDTO);
    List<ProjectMilestoneDTO> getAllProjectWithMilestone();
    List<ProjectDTO> getProjectWithUserId(String userId);
    List<ProjectDTO> getProjectsByDepartmentId(String departmentId);
}
