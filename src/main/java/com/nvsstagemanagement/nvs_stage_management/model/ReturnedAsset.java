package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
public class ReturnedAsset {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "ReturnedAssetID", nullable = false, length = 50)
    private String returnedAssetID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TaskID", nullable = false)
    private Task taskID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AssetID", nullable = false)
    private Asset assetID;

    @NotNull
    @Column(name = "ReturnTime", nullable = false)
    private Instant returnTime;

    @NotNull
    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Nationalized
    @Lob
    @Column(name = "Discription")
    private String discription;

}