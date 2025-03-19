package com.nvsstagemanagement.nvs_stage_management.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivationUserRequest {
    String email;
    String newPassword;
}
