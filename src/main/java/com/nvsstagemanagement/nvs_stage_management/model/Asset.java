package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Asset {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "AssetID", nullable = false, length = 50)
    private String assetID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryID")
    private Category categoryID;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "AssetName", nullable = false)
    private String assetName;

    @Size(max = 255)
    @Nationalized
    @Column(name = "Model")
    private String model;

    @Size(max = 50)
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

    @Size(max = 50)
    @Nationalized
    @ColumnDefault("'Available'")
    @Column(name = "Status", length = 50)
    private String status;

    @Size(max = 255)
    @Nationalized
    @Column(name = "Location")
    private String location;

    @Size(max = 50)
    @Nationalized
    @Column(name = "CreatedBy", length = 50)
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssetTypeID")
    private AssetType assetTypeID;

    @Column(name = "Quantity")
    private Integer quantity;

    @Nationalized
    @Lob
    @Column(name = "Image")
    private String image;

}