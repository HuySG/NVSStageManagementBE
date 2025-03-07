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
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectTypeID")
    @JoinColumn(name = "ProjectTypeID", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("assetTypeID")
    @JoinColumn(name = "AssetTypeID", nullable = false)
    private AssetType assetType;

    @Column(name = "Allowed")
    private Boolean allowed;
    @Column(name = "isEssential", nullable = false)
    private Boolean isEssential;
}