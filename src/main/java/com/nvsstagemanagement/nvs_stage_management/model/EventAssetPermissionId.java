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
public class EventAssetPermissionId implements Serializable {
    private static final long serialVersionUID = -7744809916299749103L;
    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "EventTypeID", nullable = false, length = 50)
    private String eventTypeID;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "AssetTypeID", nullable = false, length = 50)
    private String assetTypeID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EventAssetPermissionId entity = (EventAssetPermissionId) o;
        return Objects.equals(this.eventTypeID, entity.eventTypeID) &&
                Objects.equals(this.assetTypeID, entity.assetTypeID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventTypeID, assetTypeID);
    }

}