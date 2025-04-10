package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectTypeDTO;
import com.nvsstagemanagement.nvs_stage_management.service.impl.ProjectTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project-types")
public class ProjectTypeController {

    private final ProjectTypeService projectTypeService;

    public ProjectTypeController(ProjectTypeService projectTypeService) {
        this.projectTypeService = projectTypeService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectTypeDTO>> getAllProjectTypes() {
        List<ProjectTypeDTO> dtos = projectTypeService.getAllProjectTypes();
        return ResponseEntity.ok(dtos);
    }
}
