package com.nvsstagemanagement.nvs_stage_management.dto.user;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    private String id;

    @Size(min = 4, message = "FullNAME_INVALID")
    private String fullName;

    private LocalDate dayOfBirth;

    @NotBlank(message = "Email is required!")
    @Email(message = "Invalid email!!")
    private String email;

    @NotBlank(message = "password is required!")
    @Size(min = 6, message = "INVALID_PASSWORD")
    private String password;

    private String departmentId;

    private String pictureProfile;

    private Instant createDate;

    private String roleId;

    private String status;

    private String taskUsers;

}
