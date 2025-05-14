package com.nvsstagemanagement.nvs_stage_management.dto.returnAsset;

import com.nvsstagemanagement.nvs_stage_management.enums.ReturnRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReturnRequestResponseDTO {
    private String requestId;
    private String assetId;
    private String taskId;
    private String staffId;
    private String description;
    private String conditionNote;
    private String imageUrl;
    private ReturnRequestStatus status;
    private LocalDateTime requestTime;
    private String rejectReason;
    private LocalDateTime processedTime;
}
