package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Nationalized;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAssetPermissionId implements Serializable {
    private static final long serialVersionUID = -593449931199565504L;

    @NotNull
    @Column(name = "ProjectTypeID", nullable = false)
    private Integer projectTypeID;

    @Size(max = 50)
    @NotNull
    @Column(name = "CategoryID", nullable = false, length = 50, columnDefinition = "nvarchar(50)")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String categoryID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProjectAssetPermissionId that = (ProjectAssetPermissionId) o;
        return Objects.equals(projectTypeID, that.projectTypeID) &&
                Objects.equals(categoryID, that.categoryID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectTypeID, categoryID);
    }

}