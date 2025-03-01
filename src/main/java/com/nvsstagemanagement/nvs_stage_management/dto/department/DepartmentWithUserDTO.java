package com.nvsstagemanagement.nvs_stage_management.dto.department;

import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import lombok.Data;

@Data
public class DepartmentWithUserDTO extends DepartmentDTO{
    private UserDTO leader;
}
