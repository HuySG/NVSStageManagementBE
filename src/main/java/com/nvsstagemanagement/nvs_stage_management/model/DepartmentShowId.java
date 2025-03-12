package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentShowId implements Serializable {
    private static final long serialVersionUID = -3108767206963705409L;
    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "ShowId", nullable = false, length = 50)
    private String showId;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "DepartmentId", nullable = false, length = 50)
    private String departmentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DepartmentShowId entity = (DepartmentShowId) o;
        return Objects.equals(this.departmentId, entity.departmentId) &&
                Objects.equals(this.showId, entity.showId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departmentId, showId);
    }

}