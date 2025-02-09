package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
public class AssetUsageHistory {
    @Id
    @Nationalized
    @Column(name = "UsageID", nullable = false, length = 50)
    private String usageID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssetID")
    private Asset assetID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User userID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LocationID")
    private Location locationID;

    @Column(name = "StartDate")
    private Instant startDate;

    @Column(name = "EndDate")
    private Instant endDate;

    @Nationalized
    @ColumnDefault("'In Use'")
    @Column(name = "Status", length = 50)
    private String status;

}