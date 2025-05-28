package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.project.*;

import java.util.List;

public interface IProjectService {
    List<ProjectDTO> getAllProject();
    ProjectDTO createProject(CreateProjectDTO createProjectDTO);
    List<DepartmentProjectDTO> assignDepartmentToProject(String projectID, DepartmentProjectDTO departmentProjectDTO);
    List<ProjectMilestoneDepartmentDTO> getAllProjectWithMilestone();
    List<ProjectDepartmentDTO> getProjectWithUserId(String userId);
    List<ProjectDepartmentDTO> getProjectsByDepartmentId(String departmentId);
    ProjectMilestoneDepartmentDTO getProjectWithMilestones(String projectId);
    ProjectMilestoneDepartmentDTO getProjectByMilestoneId(String milestoneId);
    ProjectDTO updateProject(String projectId, UpdateProjectDTO updateProjectDTO);
}
