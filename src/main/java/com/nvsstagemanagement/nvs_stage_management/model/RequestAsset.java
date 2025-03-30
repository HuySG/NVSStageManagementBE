package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.enums.BookingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.List;

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
    @OneToMany(mappedBy = "requestAsset", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestAssetCategory> requestAssetCategories;
    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @Column(name = "Title")
    private String title;

    @Column(name = "StartTime")
    private Instant startTime;

    @Column(name = "EndTime")
    private Instant endTime;

    @Column(name = "Status")
    private String status;
    @Column(name = "RequestTime")
    private Instant requestTime;
    @Column(name = "CreateBy")
    private String createBy;
    @Enumerated(EnumType.STRING)
    @Column(name = "BookingType", length = 20)
    private BookingType bookingType;
    @Column(name = "RecurrenceCount")
    private Integer recurrenceCount;
    @Column(name = "RecurrenceInterval")
    private Integer recurrenceInterval;
}