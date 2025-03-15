package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentWithUserDTO;
import com.nvsstagemanagement.nvs_stage_management.service.impl.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<DepartmentWithUserDTO>> getDepartmentWithUser() {
        List<DepartmentWithUserDTO> result = departmentService.getDepartmentWithUser();
        return ResponseEntity.ok(result);
    }
//    @GetMapping
//    public ResponseEntity<List<DepartmentDTO>> getAllDepartment() {
//        List<DepartmentDTO> result = departmentService.getDepartments();
//        return ResponseEntity.ok(result);
//    }
}
