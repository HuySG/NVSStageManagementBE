package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.enums.ProjectStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Project")
public class Project {
    @Id
    @Size(max = 50)
    @Column(name = "ProjectID", nullable = false, length = 50,columnDefinition = "nvarchar(50)")
    private String projectID;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "Title", nullable = false)
    private String title;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @Nationalized
    @Lob
    @Column(name = "Content")
    private String content;

    @Column(name = "StartTime")
    private Instant startTime;

    @Column(name = "EndTime")
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 50)
    private ProjectStatus status;

    @Column(name = "ActualEndTime")
    private Instant actualEndTime;

    @Size(max = 50)
    @Nationalized
    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProjectTypeID", nullable = false)
    private ProjectType projectType;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Milestone> milestones;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DepartmentProject> departmentProjects;
}