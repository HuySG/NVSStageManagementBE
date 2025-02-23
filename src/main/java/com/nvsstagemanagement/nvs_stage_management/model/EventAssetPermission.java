package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class EventAssetPermission {
    @EmbeddedId
    private EventAssetPermissionId id;

    @MapsId("eventTypeID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EventTypeID", nullable = false)
    private EventType eventTypeID;

    @MapsId("assetTypeID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AssetTypeID", nullable = false)
    private AssetType assetTypeID;

    @Column(name = "Allowed")
    private Boolean allowed;

}