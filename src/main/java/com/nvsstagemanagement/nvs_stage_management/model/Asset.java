package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "Asset")
public class Asset {
    @Id
    @Nationalized
    @Column(name = "AssetID", nullable = false, length = 50)
    private String assetID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CategoryID")
    private Category category;

    @Nationalized
    @Column(name = "AssetName", nullable = false)
    private String assetName;

    @Nationalized
    @Column(name = "Model")
    private String model;

    @Nationalized
    @Column(name = "Code", length = 50)
    private String code;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @Column(name = "Price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "BuyDate")
    private LocalDate buyDate;

    @Nationalized
    @ColumnDefault("'Available'")
    @Column(name = "Status", length = 50)
    private String status;

    @Nationalized
    @Column(name = "Location")
    private String location;

    @Nationalized
    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AssetTypeID")
    private AssetType assetType;

    @Column(name = "Quantity")
    private Integer quantity;

    @Nationalized
    @Lob
    @Column(name = "Image")
    private String image;

}