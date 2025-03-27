package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
public class RequestAsset {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "RequestId", nullable = false, length = 50)
    private String requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskId")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssetId")
    private Asset asset;
    @ManyToOne
    @JoinColumn(name = "categoryID")
    private Category category;
    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @Column(name = "Title")
    private String title;

    @Column(name = "StartTime")
    private Instant startTime;
    @Column(name = "Quantity")
    private int quantity;

    @Column(name = "EndTime")
    private Instant endTime;

    @Column(name = "Status")
    private String status;
    @Column(name = "RequestTime")
    private Instant requestTime;
    @Column(name = "CreateBy")
    private String createBy;

}