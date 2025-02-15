package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "AssetTypies")
public class AssetType {
    @Id
    @Nationalized
    @Column(name = "ID", nullable = false, length = 50)
    private String id;

    @Nationalized
    @Column(name = "Name", length = 50)
    private String name;

}