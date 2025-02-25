package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "AssetType")
@AllArgsConstructor
@NoArgsConstructor
public class AssetType {
    @Id
    @Nationalized
    @Column(name = "AssetTypeID", nullable = false, length = 50)
    private String assetTypeID;

    @Nationalized
    @Column(name = "Name", length = 50)
    private String name;

}