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
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ShowAssetPermissionId implements Serializable {
    private static final long serialVersionUID = -593449931199565504L;
    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "ShowTypeID", nullable = false, length = 50)
    private String showTypeID;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "AssetTypeID", nullable = false, length = 50)
    private String assetTypeID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ShowAssetPermissionId entity = (ShowAssetPermissionId) o;
        return Objects.equals(this.showTypeID, entity.showTypeID) &&
                Objects.equals(this.assetTypeID, entity.assetTypeID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(showTypeID, assetTypeID);
    }

}