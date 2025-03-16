package com.nvsstagemanagement.nvs_stage_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nvsstagemanagement.nvs_stage_management.enums.TaskEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "TaskID", nullable = false, length = 50,columnDefinition = "nvarchar(50)")
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

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;
    @Size(max = 50)
    @Column(name = "Assignee")
    private String assignee;

    @Nationalized
    @ColumnDefault("'Pending'")
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "Status", length = 50)
    private TaskEnum status;
    @Size(max = 50)
    @Column(name = "CreateBy", length = 50)
    private String createBy;

    @Column(name = "CreateDate")
    private LocalDateTime createDate;

    @Size(max = 50)
    @Column(name = "UpdateBy", length = 50)
    private String updateBy;

    @Column(name = "UpdateDate")
    private LocalDateTime updateDate;
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<TaskUser> taskUsers;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MilestoneID", nullable = false)
    private Milestone milestone;
}