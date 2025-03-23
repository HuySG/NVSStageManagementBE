package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.project.DepartmentProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectMilestoneDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    private final IProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> shows = projectService.getAllProject();
        return ResponseEntity.ok(shows);
    }
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO){
        ProjectDTO createdShow = projectService.createProject(projectDTO);
        return new ResponseEntity<>(createdShow, HttpStatus.CREATED);
    }
    @PostMapping("/assign")
    public ResponseEntity<List<DepartmentProjectDTO>> assignDepartmentToShow(@RequestParam String projectID,@RequestBody DepartmentProjectDTO departmentProjectDTO){
        List<DepartmentProjectDTO> departmentToShow = projectService.assignDepartmentToProject(projectID,departmentProjectDTO);
        return ResponseEntity.ok(departmentToShow);
    }
    @GetMapping("/project-milestone")
    public ResponseEntity<List<ProjectMilestoneDTO>> getAllShowsWithTasks() {
        List<ProjectMilestoneDTO> shows = projectService.getAllProjectWithMilestone();
        return ResponseEntity.ok(shows);
    }
    @GetMapping("/userId")
    public ResponseEntity<List<ProjectDTO>> getShowWithUserId(@RequestParam String userId) {
        List<ProjectDTO> projectDTOS = projectService.getProjectWithUserId(userId);
        return ResponseEntity.ok(projectDTOS);
    }
    @GetMapping("/department")
    public ResponseEntity<List<ProjectDTO>> getProjectsByDepartmentId(@RequestParam String Id) {
        List<ProjectDTO> projects = projectService.getProjectsByDepartmentId(Id);
        return ResponseEntity.ok(projects);
    }
}
