package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProjectAssetPermission {
    @EmbeddedId
    private ProjectAssetPermissionId id;

    @Column(name = "Allowed")
    private Boolean allowed;

}