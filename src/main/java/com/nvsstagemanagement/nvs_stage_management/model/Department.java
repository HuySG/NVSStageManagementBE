package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Getter
@Setter
@Entity
public class Department {
    @Id
    @Size(max = 50)
    @Column(name = "DepartmentId", nullable = false, length = 50,columnDefinition = "nvarchar(50)")
    private String departmentId;

    @Size(max = 250)
    @Nationalized
    @Column(name = "Name", length = 250)
    private String name;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DepartmentProject> departmentProjects;
}