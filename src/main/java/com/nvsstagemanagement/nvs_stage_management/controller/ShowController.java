package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.show.DepartmentShowDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.show.ShowDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.show.ShowTaskDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ShowController {
    private final IShowService showService;

    @GetMapping
    public ResponseEntity<List<ShowDTO>> getAllProjects() {
        List<ShowDTO> projects = showService.getAllShow();
        return ResponseEntity.ok(projects);
    }
    @PostMapping
    public ResponseEntity<ShowDTO> createProject(@RequestBody ShowDTO showDTO){
        ShowDTO createdProject = showService.createShow(showDTO);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }
    @PostMapping("/assign")
    public ResponseEntity<DepartmentShowDTO> assignDepartmentToProject(@RequestBody DepartmentShowDTO departmentShowDTO){
        DepartmentShowDTO departmentToProject = showService.assignDepartmentToShow(departmentShowDTO);
        return ResponseEntity.ok(departmentToProject);
    }
    @GetMapping("/project-task")
    public ResponseEntity<List<ShowTaskDTO>> getAllProjectsWithTasks() {
        List<ShowTaskDTO> projects = showService.getAllShowWithTasks();
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/userId")
    public ResponseEntity<List<ShowDTO>> getProjectWithUserId(@RequestParam String userId) {
        List<ShowDTO> showDTOS = showService.getShowWithUserId(userId);
        return ResponseEntity.ok(showDTOS);
    }
}
