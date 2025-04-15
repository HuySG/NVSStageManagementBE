package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.enums.DependencyLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TaskDependency")
@IdClass(TaskDependencyId.class)
public class TaskDependency {
    @Id
    @Column(name = "TaskID", length = 50,columnDefinition = "nvarchar(50)")
    private String taskID;

    @Id
    @Column(name = "DependsOnTaskID", length = 50,columnDefinition = "nvarchar(50)")
    private String dependsOnTaskID;

    @Enumerated(EnumType.STRING)
    @Column(name = "DependencyLevel", length = 20)
    private DependencyLevel dependencyLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskID", insertable = false, updatable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DependsOnTaskID", insertable = false, updatable = false)
    private Task dependsOnTask;
}
