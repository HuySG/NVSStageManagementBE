package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Event")
public class Event {
    @Id
    @Nationalized
    @Column(name = "EventID", nullable = false, length = 50)
    private String eventID;

    @Nationalized
    @Column(name = "EventName", nullable = false)
    private String eventName;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @Column(name = "StartTime", nullable = false)
    private Instant startTime;

    @Column(name = "EndTime", nullable = false)
    private Instant endTime;

    @Column(name = "TicketPrice", precision = 10, scale = 2)
    private BigDecimal ticketPrice;

    @Nationalized
    @Column(name = "Location")
    private String location;

    @Nationalized
    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

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

}