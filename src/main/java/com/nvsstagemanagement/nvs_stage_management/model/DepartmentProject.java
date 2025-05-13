package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "DepartmentProject")
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentProject {
    @EmbeddedId
    private DepartmentProjectId id;
    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "ProjectId", referencedColumnName = "ProjectID", columnDefinition = "nvarchar(50)")
    private Project project;

    @ManyToOne
    @MapsId("departmentId")
    @JoinColumn(name = "DepartmentId", referencedColumnName = "DepartmentId", columnDefinition = "nvarchar(50)")
    private Department department;

}