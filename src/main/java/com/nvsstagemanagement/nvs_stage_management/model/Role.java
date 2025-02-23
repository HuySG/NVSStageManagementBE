package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "Role")
public class Role {
    @Id
    @Column(name = "RoleID", nullable = false, length = 50)
    private String roleID;
    @Column(name = "RoleName", length = 100)
    private String roleName;
}
