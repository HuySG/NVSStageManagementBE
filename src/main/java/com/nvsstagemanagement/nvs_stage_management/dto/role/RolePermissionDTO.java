package com.nvsstagemanagement.nvs_stage_management.dto.role;

import com.nvsstagemanagement.nvs_stage_management.dto.permission.PermissionDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import lombok.Data;

import java.util.List;

@Data
public class RolePermissionDTO extends RoleDTO {
    private List<PermissionDTO> permissions;
}
