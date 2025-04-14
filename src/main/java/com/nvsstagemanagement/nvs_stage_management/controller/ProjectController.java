package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.project.*;
import com.nvsstagemanagement.nvs_stage_management.exception.ApiErrorResponse;
import com.nvsstagemanagement.nvs_stage_management.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    private final IProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDepartmentDTO>> getAllProjects() {
        List<ProjectDepartmentDTO> shows = projectService.getAllProject();
        return ResponseEntity.ok(shows);
    }
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody CreateProjectDTO createProjectDTO){
        try {
            ProjectDTO createdProject = projectService.createProject(createProjectDTO);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating project: " + ex.getMessage());
        }
    }
    @PostMapping("/assign")
    public ResponseEntity<List<DepartmentProjectDTO>> assignDepartmentToShow(@RequestParam String projectID,@RequestBody DepartmentProjectDTO departmentProjectDTO){
        List<DepartmentProjectDTO> departmentToShow = projectService.assignDepartmentToProject(projectID,departmentProjectDTO);
        return ResponseEntity.ok(departmentToShow);
    }
    @GetMapping("/project-milestone")
    public ResponseEntity<List<ProjectMilestoneDepartmentDTO>> getAllProjectWithTasks() {
        List<ProjectMilestoneDepartmentDTO> shows = projectService.getAllProjectWithMilestone();
        return ResponseEntity.ok(shows);
    }
    @GetMapping("/userId")
    public ResponseEntity<List<ProjectDepartmentDTO>> getProjectWithUserId(@RequestParam String userId) {
        List<ProjectDepartmentDTO> projectDTOS = projectService.getProjectWithUserId(userId);
        return ResponseEntity.ok(projectDTOS);
    }
    @GetMapping("/department")
    public ResponseEntity<List<ProjectDepartmentDTO>> getProjectsByDepartmentId(@RequestParam String Id) {
        List<ProjectDepartmentDTO> projects = projectService.getProjectsByDepartmentId(Id);
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/{projectId}/details")
    public ResponseEntity<ProjectMilestoneDepartmentDTO> getProjectWithMilestones(@PathVariable String projectId) {
        ProjectMilestoneDepartmentDTO dto = projectService.getProjectWithMilestones(projectId);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/milestone/{milestoneId}")
    public ResponseEntity<?> getProjectByMilestoneId(@PathVariable String milestoneId) {
        try {
            ProjectMilestoneDepartmentDTO dto = projectService.getProjectByMilestoneId(milestoneId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            ApiErrorResponse error = new ApiErrorResponse("PROJECT_NOT_FOUND", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ApiErrorResponse error = new ApiErrorResponse("INTERNAL_SERVER_ERROR", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @PutMapping("/{projectId}")
    public ProjectDTO updateProject(
            @PathVariable String projectId,
            @RequestBody UpdateProjectDTO updateProjectDTO) {
        return projectService.updateProject(projectId, updateProjectDTO);
    }

}
