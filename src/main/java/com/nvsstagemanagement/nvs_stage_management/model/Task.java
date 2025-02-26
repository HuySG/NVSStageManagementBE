package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.enums.TaskEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Task {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "TaskID", nullable = false, length = 50)
    private String taskID;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "Title", nullable = false)
    private String title;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @Size(max = 50)
    @Nationalized
    @Column(name = "Priority", length = 50)
    private String priority;

    @Size(max = 50)
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
    private TaskEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectId")
    private Project project;

    @Nationalized
    @Lob
    @Column(name = "Attachments")
    private String attachments;

}