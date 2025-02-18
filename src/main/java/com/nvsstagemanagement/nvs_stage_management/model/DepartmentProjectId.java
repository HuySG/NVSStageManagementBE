package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentProjectId implements Serializable {
    private static final long serialVersionUID = -3108767206963705409L;
    @Nationalized
    @Column(name = "ProjectId", nullable = false, length = 50)
    private String projectId;

    @Nationalized
    @Column(name = "DepartmentId", nullable = false, length = 50)
    private String departmentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DepartmentProjectId entity = (DepartmentProjectId) o;
        return Objects.equals(this.departmentId, entity.departmentId) &&
                Objects.equals(this.projectId, entity.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departmentId, projectId);
    }

}