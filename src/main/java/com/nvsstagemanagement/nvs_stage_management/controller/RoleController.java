//package com.nvsstagemanagement.nvs_stage_management.controller;
//
//
//import com.nvsstagemanagement.nvs_stage_management.dto.role.RoleDTO;
//import com.nvsstagemanagement.nvs_stage_management.service.IRoleService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/roles")
//@RequiredArgsConstructor
//public class RoleController {
//    private final IRoleService roleService;
//
//    @GetMapping
//    public ResponseEntity<List<RoleDTO>> getAllRole() {
//        List<RoleDTO> result = roleService.getRoles();
//        return ResponseEntity.ok(result);
//    }
//}
