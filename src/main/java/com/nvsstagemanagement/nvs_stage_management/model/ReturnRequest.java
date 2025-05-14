package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.enums.ReturnRequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ReturnRequest")
@Data
@NoArgsConstructor
public class ReturnRequest {
    @Id
    private String requestId;

    @ManyToOne
    @JoinColumn(name = "AsssetID")
    private Asset asset;

    @ManyToOne
    @JoinColumn(name = "TaskID")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "StaffID")
    private User staff;

    @ManyToOne
    @JoinColumn(name = "LeaderID")
    private User leader;

    private String description;
    private String conditionNote;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ReturnRequestStatus status;

    private LocalDateTime requestTime;
    private LocalDateTime processedTime;
    private String rejectReason;
    private String leaderNote;
    private BigDecimal damageFee;
    private BigDecimal lateFee;
}