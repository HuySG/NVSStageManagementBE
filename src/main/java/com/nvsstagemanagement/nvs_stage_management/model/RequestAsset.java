package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.enums.BookingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDate;
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
    @Column(name = "Title", columnDefinition = "NVARCHAR(MAX)")
    private String title;

    @Nationalized
    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;
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
    @Nationalized
    @Column(name = "BookingType", columnDefinition = "NVARCHAR(20)", length = 20)
    private BookingType bookingType;

    @Column(name = "RecurrenceCount")
    private Integer recurrenceCount;
    @Column(name = "RecurrenceInterval")
    private Integer recurrenceInterval;
    @Column(name = "ApprovedByDL", length = 50)
    private String approvedByDL;
    @Column(name = "ApprovedByAM", length = 50)
    private String approvedByAM;
    @Column(name = "ApprovedByDLTime")
    private Instant approvedByDLTime;

    @Column(name = "ApprovedByAMTime")
    private Instant approvedByAMTime;
    @Nationalized
    @Column(name = "RejectionReason")
    private String rejectionReason;

    @Column(name = "ExpectedReturnDate")
    private Instant expectedReturnDate;
}