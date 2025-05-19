package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffAllocatedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffBorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.request.ApiResponse;
import com.nvsstagemanagement.nvs_stage_management.exception.ApiErrorResponse;
import com.nvsstagemanagement.nvs_stage_management.service.IStaffAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff-assets")
@RequiredArgsConstructor
@Slf4j
public class StaffAssetController {
    private final IStaffAssetService staffAssetService;


    @GetMapping("/{staffId}/allocations")
    public ResponseEntity<?> getAllocations(@PathVariable String staffId) {
        try {
            List<StaffAllocatedAssetDTO> list = staffAssetService.getAllocatedAssetsByStaff(staffId);
            return ResponseEntity.ok(ApiResponse.<List<StaffAllocatedAssetDTO>>builder()
                    .code(1000)
                    .message("Lấy danh sách tài sản cấp phát thành công")
                    .result(list)
                    .build()
            );
        } catch (RuntimeException e) {
            log.error("Error fetching allocations for {}", staffId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponse("GET_ALLOCATIONS_ERROR", e.getMessage()));
        }
    }
}
