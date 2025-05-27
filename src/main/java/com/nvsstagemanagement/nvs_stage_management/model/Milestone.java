package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.enums.MilestoneStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Milestone")
public class Milestone {
    @Id
    @Size(max = 50)
    @Column(name = "MilestoneID",length = 50,columnDefinition = "nvarchar(50)")
    private String milestoneID;

    @Nationalized
    @Column(name = "Title", nullable = false)
    private String title;
    @Column(name = "Description", nullable = false)
    private String description;

    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 50)
    private MilestoneStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectID", nullable = false)
    private Project project;
    @OneToMany(mappedBy = "milestone", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Task> tasks;

}
