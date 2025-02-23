package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.role.RoleDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "[User]")
public class User {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "ID", nullable = false, length = 50)
    private String id;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "FullName", nullable = false)
    private String fullName;

    @Column(name = "DayOfBirth")
    private LocalDate dayOfBirth;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "Email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "Password", nullable = false)
    private String password;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DepartmentId", nullable = false)
    private Department department;

    @Size(max = 255)
    @Nationalized
    @Column(name = "PictureProfile")
    private String pictureProfile;

    @ColumnDefault("getdate()")
    @Column(name = "CreateDate")
    private Instant createDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoleID", nullable = false)
    private Role roleID;


    @Size(max = 50)
    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;

}