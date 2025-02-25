package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class TaskUserId implements Serializable {
    private static final long serialVersionUID = 8335173316530395536L;
    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "TaskId", nullable = false, length = 50)
    private String taskId;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "UserId", nullable = false, length = 50)
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