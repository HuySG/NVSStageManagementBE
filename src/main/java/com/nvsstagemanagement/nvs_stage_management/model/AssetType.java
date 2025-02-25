package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class AssetType {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "AssetTypeID", nullable = false, length = 50)
    private String assetTypeID;

    @Size(max = 50)
    @Nationalized
    @Column(name = "Name", length = 50)
    private String name;

    @OneToMany(mappedBy = "assetTypeID")
    private Set<Asset> assets = new LinkedHashSet<>();

    @OneToMany(mappedBy = "assetTypeID")
    private Set<ProjectAssetPermission> projectAssetPermissions = new LinkedHashSet<>();

}