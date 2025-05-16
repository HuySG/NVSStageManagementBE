package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.enums.BookingType;
import com.nvsstagemanagement.nvs_stage_management.enums.RecurrenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "RequestTime")
    private Instant requestTime;

    @Column(name = "CreateBy", length = 50)
    private String createBy;

    @Enumerated(EnumType.STRING)
    @Nationalized
    @Column(name = "BookingType", length = 20, columnDefinition = "NVARCHAR(20)")
    private BookingType bookingType;


    @Enumerated(EnumType.STRING)
    @Column(name = "RecurrenceType", length = 20)
    private RecurrenceType recurrenceType;

    @Column(name = "RecurrenceInterval")
    private Integer recurrenceInterval;

    @Column(name = "RecurrenceEndDate")
    private LocalDate recurrenceEndDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "RequestAsset_SelectedDays",
            joinColumns = @JoinColumn(name = "RequestId")
    )
    @Column(name = "DayOfWeek", length = 10)
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> selectedDaysOfWeek;

    @Column(name = "DayOfMonth")
    private Integer dayOfMonth;

    @Column(name = "FallbackToLastDay")
    private Boolean fallbackToLastDay;


    @Column(name = "RecurrenceCount")
    private Integer recurrenceCount;

    @Column(name = "ApprovedByDL", length = 50)
    private String approvedByDL;

    @Column(name = "ApprovedByAM", length = 50)
    private String approvedByAM;

    @Column(name = "ApprovedByDLTime")
    private Instant approvedByDLTime;

    @Column(name = "ApprovedByAMTime")
    private Instant approvedByAMTime;

    @Nationalized
    @Column(name = "RejectionReason", columnDefinition = "NVARCHAR(MAX)")
    private String rejectionReason;

    @Column(name = "ExpectedReturnDate")
    private Instant expectedReturnDate;
}