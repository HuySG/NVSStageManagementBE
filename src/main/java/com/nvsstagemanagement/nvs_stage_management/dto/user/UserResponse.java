package com.nvsstagemanagement.nvs_stage_management.dto.user;

import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    private String id;

    private String fullName;

    private LocalDate dayOfBirth;

    private String email;

    private String password;

    private DepartmentDTO department;

    private String pictureProfile;

    private Instant createDate;

    private String roleID;

    private String status;
}
