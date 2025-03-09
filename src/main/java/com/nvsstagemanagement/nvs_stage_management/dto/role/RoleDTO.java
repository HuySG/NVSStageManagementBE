package com.nvsstagemanagement.nvs_stage_management.dto.role;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private Integer id;
    private String roleName;
    private String permission;
}
