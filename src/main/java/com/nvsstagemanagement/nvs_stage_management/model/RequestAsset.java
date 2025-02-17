package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "RequestAsset")
public class RequestAsset {
    @Id
    @Nationalized
    @Column(name = "RequestId", nullable = false, length = 50)
    private String requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskId")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssetId")
    private Asset asset;

    @Column(name = "Quantity")
    private Integer quantity;

    @Nationalized
    @Lob
    @Column(name = "Discription")
    private String discription;

    @Column(name = "StartTime")
    private Instant startTime;

    @Column(name = "EndTime")
    private Instant endTime;

}