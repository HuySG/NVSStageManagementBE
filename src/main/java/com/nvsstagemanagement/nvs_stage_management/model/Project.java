package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
public class Project {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "ProjectID", nullable = false, length = 50)
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

    @Size(max = 50)
    @Nationalized
    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

}