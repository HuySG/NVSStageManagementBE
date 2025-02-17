package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "Task")
public class Task {
    @Id
    @Nationalized
    @Column(name = "TaskID", nullable = false, length = 50)
    private String taskID;

    @Nationalized
    @Column(name = "Title", nullable = false)
    private String title;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @Nationalized
    @Column(name = "Assignee", length = 50)
    private String assignee;

    @Nationalized
    @Column(name = "Priority", length = 50)
    private String priority;

    @Nationalized
    @Column(name = "Tag", length = 50)
    private String tag;

    @Nationalized
    @Lob
    @Column(name = "Content")
    private String content;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Nationalized
    @ColumnDefault("'Pending'")
    @Column(name = "Status", length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectId")
    private Project project;

}