package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Nationalized;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUserId implements Serializable {
    @Serial
    private static final long serialVersionUID = 8335173316530395536L;

    @NotNull
    @Nationalized
    @Column(name = "TaskID", nullable = false, length = 50 ,columnDefinition = "nvarchar(50)")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String taskId;


    @NotNull
    @Nationalized
    @Column(name = "ID", nullable = false, length = 50 ,columnDefinition = "nvarchar(50)")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TaskUserId entity = (TaskUserId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.taskId, entity.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, taskId);
    }

}