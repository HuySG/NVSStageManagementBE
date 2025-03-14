package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "DepartmentProject")
public class DepartmentProject {
    @EmbeddedId
    private DepartmentProjectId id;
    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "ProjectId", referencedColumnName = "projectId", columnDefinition = "nvarchar(50)")
    private Project project;

    @ManyToOne
    @MapsId("departmentId")
    @JoinColumn(name = "DepartmentId", referencedColumnName = "DepartmentId", columnDefinition = "nvarchar(50)")
    private Department department;

}