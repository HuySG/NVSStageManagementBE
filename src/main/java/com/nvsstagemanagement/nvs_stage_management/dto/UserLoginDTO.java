package com.nvsstagemanagement.nvs_stage_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    @NotBlank(message = "Tài khoản không được để trống")
    private String username;
    @NotBlank(message = "Mật khẩu không được để trống")

    private String password;
}