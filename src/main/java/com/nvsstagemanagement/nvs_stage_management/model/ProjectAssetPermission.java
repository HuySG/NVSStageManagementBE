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
    @JoinColumn(name = "ProjectTypeID", referencedColumnName = "projectTypeID")
    private ProjectType projectType;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "CategoryID",referencedColumnName = "categoryId",columnDefinition = "nvarchar(50)")
    private Category category;

    @Column(name = "Allowed")
    private Boolean allowed;

    @Column(name = "isEssential", nullable = false)
    private Boolean isEssential;
}