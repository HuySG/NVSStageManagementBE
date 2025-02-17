package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Project")
public class Project {
    @Id
    @Nationalized
    @Column(name = "ProjectID", nullable = false, length = 50)
    private String projectID;

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

    @Nationalized
    @Column(name = "Department")
    private String department;

    @Nationalized
    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

}