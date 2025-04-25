package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUser {
    @EmbeddedId
    private TaskUserId id;
    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "TaskId", referencedColumnName = "TaskID",columnDefinition = "nvarchar(50)")
    private Task task;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "UserId", referencedColumnName = "ID",nullable = false,columnDefinition = "nvarchar(50)")
    private User user;

}