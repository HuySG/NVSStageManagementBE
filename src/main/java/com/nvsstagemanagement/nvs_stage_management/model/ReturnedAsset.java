package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;
    @Column(name = "ActualReturnDate")
    private Instant actualReturnDate;
    @Column(name = "LatePenaltyFee", precision = 15, scale = 2)
    private BigDecimal latePenaltyFee;
    @Nationalized
    @Lob
    @Column(name = "ConditionAfter")
    private String conditionAfter;
    @Nationalized
    @Lob
    @Column(name = "ImageAfter")
    private String imageAfter;


}