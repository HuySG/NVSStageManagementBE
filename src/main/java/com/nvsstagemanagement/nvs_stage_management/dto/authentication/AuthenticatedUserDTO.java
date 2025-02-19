package com.nvsstagemanagement.nvs_stage_management.dto.authentication;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUserDTO {
    @NonNull
    private String token;
    private List<String> roles;
}
