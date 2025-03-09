package com.nvsstagemanagement.nvs_stage_management.dto.request;


import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {


    private String fullName;

    private LocalDate dayOfBirth;

    private String email;

    private String password;

    private String pictureProfile;


}
