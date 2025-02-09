package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {
    @Id
    @Nationalized
    @Column(name = "ID", nullable = false, length = 50)
    private String id;

    @Nationalized
    @Column(name = "FullName", nullable = false)
    private String fullName;

    @Column(name = "DayOfBirth")
    private LocalDate dayOfBirth;

    @Nationalized
    @Column(name = "Email", nullable = false)
    private String email;

    @Nationalized
    @Column(name = "Password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DepartmentId", nullable = false)
    private Department department;

    @Nationalized
    @Column(name = "PictureProfile")
    private String pictureProfile;

    @ColumnDefault("getdate()")
    @Column(name = "CreateDate")
    private Instant createDate;

    @Nationalized
    @Column(name = "Role", nullable = false, length = 50)
    private String role;

    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;

}