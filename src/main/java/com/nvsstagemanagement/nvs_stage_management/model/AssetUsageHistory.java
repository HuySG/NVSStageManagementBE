package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
public class AssetUsageHistory {
    @Id
    @Size(max = 50)
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
    private OffsetDateTime startDate;

    @Column(name = "EndDate")
    private OffsetDateTime endDate;

    @Size(max = 50)
    @Nationalized
    @ColumnDefault("'In Use'")
    @Column(name = "Status", length = 50)
    private String status;

}