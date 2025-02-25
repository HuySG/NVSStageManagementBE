package com.nvsstagemanagement.nvs_stage_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Size(max = 50)
    @Nationalized
    @ColumnDefault("'Pending'")
    @Column(name = "Status", length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectId")
    @JsonIgnore
    private Project project;

    @Nationalized
    @Lob
    @Column(name = "Attachments")
    private String attachments;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskUser> taskUsers;
}