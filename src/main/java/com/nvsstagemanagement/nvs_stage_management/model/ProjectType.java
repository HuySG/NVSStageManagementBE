package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Getter
@Setter
@Entity
public class ProjectType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProjectTypeID", nullable = false)
    private Integer projectTypeID;

    @NotNull
    @Nationalized
    @Lob
    @Column(name = "TypeName", nullable = false)
    private String typeName;
    @OneToMany(mappedBy = "projectType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Project> projects;

}