package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
public class Event {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "EventID", nullable = false, length = 50)
    private String eventID;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "EventName", nullable = false)
    private String eventName;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @NotNull
    @Column(name = "StartTime", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "EndTime", nullable = false)
    private Instant endTime;

    @Size(max = 255)
    @Nationalized
    @Column(name = "Location")
    private String location;

    @Size(max = 50)
    @Nationalized
    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

    @Size(max = 50)
    @Nationalized
    @ColumnDefault("'Scheduled'")
    @Column(name = "Status", length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectId")
    private Project project;

    @Nationalized
    @Lob
    @Column(name = "Image")
    private String image;

    @Size(max = 10)
    @Nationalized
    @Column(name = "RecurrenceType", length = 10)
    private String recurrenceType;
    @ManyToOne
    @JoinColumn(name = "EventTypeID")
    private EventType eventType;
}