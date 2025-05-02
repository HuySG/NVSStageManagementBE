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
public class DepartmentProjectId implements Serializable {
    @Serial
    private static final long serialVersionUID = -3108767206963705409L;

    @NotNull
    @Column(name = "ProjectID", nullable = false, length = 50,columnDefinition = "nvarchar(50)")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String projectID;


    @NotNull
    @Column(name = "DepartmentId", nullable = false, length = 50,columnDefinition = "nvarchar(50)")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String departmentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DepartmentProjectId entity = (DepartmentProjectId) o;
        return Objects.equals(this.departmentId, entity.departmentId) &&
                Objects.equals(this.projectID, entity.projectID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departmentId, projectID);
    }

}