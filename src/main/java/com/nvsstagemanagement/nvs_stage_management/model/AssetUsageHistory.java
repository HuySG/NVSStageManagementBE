package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
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
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectID", nullable = false)
    private Project project;
    @Column(name = "StartDate")
    private Instant startDate;

    @Column(name = "EndDate")
    private Instant endDate;

    @Size(max = 50)
    @Nationalized
    @ColumnDefault("'In Use'")
    @Column(name = "Status", length = 50)
    private String status;

}