package com.nvsstagemanagement.nvs_stage_management.dto.user;


import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.role.RoleDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskUserDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Role;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;

    private String fullName;

    private LocalDate dayOfBirth;

    private String email;

    private String password;

    private DepartmentDTO department;

    private String pictureProfile;

    private Instant createDate;

    private RoleDTO role;

    private String status;
    private List<TaskUserDTO> taskUsers;

}
