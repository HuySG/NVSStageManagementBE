package com.nvsstagemanagement.nvs_stage_management.mapper;


import com.nvsstagemanagement.nvs_stage_management.dto.request.RoleRequest;
import com.nvsstagemanagement.nvs_stage_management.dto.response.RoleResponse;
import com.nvsstagemanagement.nvs_stage_management.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
