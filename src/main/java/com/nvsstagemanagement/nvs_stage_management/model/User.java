package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "User")
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


    @Column(name = "CreateDate")
    private Instant createDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoleID", nullable = false)
    private Role role;

    @Size(max = 50)
    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;

}