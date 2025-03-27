package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private LocalDate startTime;

    @NotNull
    @Column(name = "EndTime", nullable = false)
    private LocalDate endTime;

    @Size(max = 50)
    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;

    @Nationalized
    @Lob
    @Column(name = "Image")
    private String image;
    @ManyToOne
    @JoinColumn(name = "MilestoneID", referencedColumnName = "MilestoneID")
    private Milestone milestone;
}
