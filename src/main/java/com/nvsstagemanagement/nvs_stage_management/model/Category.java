package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "CategoryID", nullable = false, length = 50)
    private String categoryID;

    @Size(max = 50)
    @Nationalized
    @Column(name = "Name", length = 50)
    private String name;
    @ManyToOne
    @JoinColumn(name = "AssetTypeID", referencedColumnName = "AssetTypeID")
    private AssetType assetType;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestAssetCategory> requestAssetCategories = new ArrayList<>();
}