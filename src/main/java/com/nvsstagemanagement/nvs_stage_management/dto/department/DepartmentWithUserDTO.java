package com.nvsstagemanagement.nvs_stage_management.dto.department;

import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentWithUserDTO extends DepartmentDTO{
    private List<UserDTO> users;
}
