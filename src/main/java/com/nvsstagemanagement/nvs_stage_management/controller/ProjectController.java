package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.DepartmentProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectTaskDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final IProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProject();
        return ResponseEntity.ok(projects);
    }
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO){
        ProjectDTO createdProject = projectService.createProject(projectDTO);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }
    @PostMapping("/assign")
    public ResponseEntity<DepartmentProjectDTO> assignDepartmentToProject(@RequestBody DepartmentProjectDTO departmentProjectDTO){
        DepartmentProjectDTO departmentToProject = projectService.assignDepartmentToProject(departmentProjectDTO);
        return ResponseEntity.ok(departmentToProject);
    }
    @GetMapping("/project-task")
    public ResponseEntity<List<ProjectTaskDTO>> getAllProjectsWithTasks() {
        List<ProjectTaskDTO> projects = projectService.getAllProjectsWithTasks();
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/userId")
    public ResponseEntity<List<ProjectDTO>> getProjectWithUserId(@RequestParam String userId) {
        List<ProjectDTO> projectDTOs = projectService.getProjectWithUserId(userId);
        return ResponseEntity.ok(projectDTOs);
    }
}
