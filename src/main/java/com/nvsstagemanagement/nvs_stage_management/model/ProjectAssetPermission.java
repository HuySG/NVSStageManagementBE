package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProjectAssetPermission {
    @EmbeddedId
    private ProjectAssetPermissionId id;

    @MapsId("projectTypeID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProjectTypeID", nullable = false)
    private ProjectType projectTypeID;

    @MapsId("assetTypeID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AssetTypeID", nullable = false)
    private AssetType assetTypeID;

    @Column(name = "Allowed")
    private Boolean allowed;

}