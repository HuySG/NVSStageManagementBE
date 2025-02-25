package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class ProjectType {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "ProjectTypeID", nullable = false, length = 50)
    private String projectTypeID;

    @NotNull
    @Nationalized
    @Lob
    @Column(name = "TypeName", nullable = false)
    private String typeName;

    @OneToMany(mappedBy = "projectTypeID")
    private Set<Project> projects = new LinkedHashSet<>();

    @OneToMany(mappedBy = "projectTypeID")
    private Set<ProjectAssetPermission> projectAssetPermissions = new LinkedHashSet<>();

}