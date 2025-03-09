package com.nvsstagemanagement.nvs_stage_management.service.impl;


import com.nvsstagemanagement.nvs_stage_management.dto.request.PermissionRequest;
import com.nvsstagemanagement.nvs_stage_management.dto.response.PermissionResponse;
import com.nvsstagemanagement.nvs_stage_management.mapper.PermissionMapper;
import com.nvsstagemanagement.nvs_stage_management.model.Permission;
import com.nvsstagemanagement.nvs_stage_management.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
