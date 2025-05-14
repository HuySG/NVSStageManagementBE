package com.nvsstagemanagement.nvs_stage_management.dto.returnAsset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequestStatisticsDTO {
    private long totalRequests;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    private long lateReturns;
    private BigDecimal totalLateFees;
    private BigDecimal totalDamageFees;
    private List<DepartmentStatistics> departmentStatistics;
    private List<ProjectStatistics> projectStatistics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentStatistics {
        private String departmentId;
        private String departmentName;
        private long totalRequests;
        private long pendingRequests;
        private long approvedRequests;
        private long rejectedRequests;
        private BigDecimal totalFees;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectStatistics {
        private String projectId; 
        private String projectName;
        private long totalRequests;
        private long pendingRequests;
        private long approvedRequests;
        private long rejectedRequests;
        private BigDecimal totalFees;
    }
}