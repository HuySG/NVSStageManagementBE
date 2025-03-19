package com.nvsstagemanagement.nvs_stage_management.mapper;


import com.nvsstagemanagement.nvs_stage_management.dto.request.PermissionRequest;
import com.nvsstagemanagement.nvs_stage_management.dto.response.PermissionResponse;
import com.nvsstagemanagement.nvs_stage_management.model.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
