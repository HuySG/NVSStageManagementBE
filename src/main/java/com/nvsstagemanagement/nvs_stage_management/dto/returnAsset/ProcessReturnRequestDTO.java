package com.nvsstagemanagement.nvs_stage_management.dto.returnAsset;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessReturnRequestDTO {
    private String requestId;
    private boolean approved;
    private String rejectReason;
    private String leaderNote;
    private BigDecimal damageFee;
}
