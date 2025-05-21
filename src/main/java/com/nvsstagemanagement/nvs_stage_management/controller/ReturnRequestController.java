package com.nvsstagemanagement.nvs_stage_management.controller;


import com.nvsstagemanagement.nvs_stage_management.dto.request.ApiResponse;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ProcessReturnRequestDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnRequestDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnRequestResponseDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnRequestStatisticsDTO;
import com.nvsstagemanagement.nvs_stage_management.exception.ApiErrorResponse;
import com.nvsstagemanagement.nvs_stage_management.service.IReturnRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/return-requests")
@RequiredArgsConstructor
@Slf4j
public class ReturnRequestController {
    private final IReturnRequestService returnRequestService;

    /**
     * Tạo yêu cầu trả tài sản mới
     */
    @PostMapping
    public ResponseEntity<?> createReturnRequest(
            @Valid @RequestBody ReturnRequestDTO dto,
            @RequestParam String staffId) {
        try {
            ReturnRequestResponseDTO response = returnRequestService.createReturnRequest(dto, staffId);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ReturnRequestResponseDTO>builder()
                    .code(1000)
                    .message("Tạo yêu cầu trả tài sản thành công")
                    .result(response)
                    .build()
            );
        } catch (RuntimeException e) {
            log.error("Error creating return request", e);
            return ResponseEntity.badRequest().body(
                new ApiErrorResponse("CREATE_RETURN_REQUEST_ERROR", e.getMessage())
            );
        }
    }

    /**
     * Xử lý yêu cầu trả tài sản (phê duyệt/từ chối)
     */
    @PostMapping("/{requestId}/process")
    public ResponseEntity<?> processReturnRequest(
            @PathVariable String requestId,
            @Valid @RequestBody ProcessReturnRequestDTO dto,
            @RequestParam String leaderId) {
        try {
            ReturnRequestResponseDTO response = returnRequestService.processReturnRequest(requestId,dto,leaderId);
            return ResponseEntity.ok(
                ApiResponse.<ReturnRequestResponseDTO>builder()
                    .code(1000)
                    .message("Xử lý yêu cầu trả tài sản thành công")
                    .result(response)
                    .build()
            );
        } catch (RuntimeException e) {
            log.error("Error processing return request", e);
            return ResponseEntity.badRequest().body(
                new ApiErrorResponse("PROCESS_RETURN_REQUEST_ERROR", e.getMessage())
            );
        }
    }

    /**
     * Lấy danh sách yêu cầu trả tài sản của một nhân viên
     */
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<?> getStaffRequests(@PathVariable String staffId) {
        List<ReturnRequestResponseDTO> requests = returnRequestService.getStaffRequests(staffId);
        if (requests.isEmpty()) {
            return ResponseEntity.ok(
                ApiResponse.<List<ReturnRequestResponseDTO>>builder()
                    .code(1000)
                    .message("Không có yêu cầu trả tài sản nào")
                    .result(Collections.emptyList())
                    .build()
            );
        }
        return ResponseEntity.ok(
            ApiResponse.<List<ReturnRequestResponseDTO>>builder()
                .code(1000)
                .message("Lấy danh sách yêu cầu trả tài sản thành công")
                .result(requests)
                .build()
        );
    }

    /**
     * Lấy danh sách yêu cầu trả tài sản đang chờ xử lý
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests() {
        List<ReturnRequestResponseDTO> requests = returnRequestService.getPendingRequests();
        if (requests.isEmpty()) {
            return ResponseEntity.ok(
                ApiResponse.<List<ReturnRequestResponseDTO>>builder()
                    .code(1000)
                    .message("Không có yêu cầu trả tài sản nào đang chờ xử lý")
                    .result(Collections.emptyList())
                    .build()
            );
        }
        return ResponseEntity.ok(
            ApiResponse.<List<ReturnRequestResponseDTO>>builder()
                .code(1000)
                .message("Lấy danh sách yêu cầu trả tài sản đang chờ xử lý thành công")
                .result(requests)
                .build()
        );
    }
    /**
     * Lấy thông tin chi tiết của một yêu cầu trả tài sản
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<?> getReturnRequestDetail(@PathVariable String requestId) {
        try {
            ReturnRequestResponseDTO response = returnRequestService.getReturnRequestById(requestId);
            return ResponseEntity.ok(
                    ApiResponse.<ReturnRequestResponseDTO>builder()
                            .code(1000)
                            .message("Lấy thông tin yêu cầu trả tài sản thành công")
                            .result(response)
                            .build()
            );
        } catch (RuntimeException e) {
            log.error("Error getting return request detail", e);
            return ResponseEntity.badRequest().body(
                    new ApiErrorResponse("GET_RETURN_REQUEST_ERROR", e.getMessage())
            );
        }
    }

    /**
     * Lấy danh sách yêu cầu trả tài sản theo phòng ban
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<?> getDepartmentRequests(@PathVariable String departmentId) {
        try {
            List<ReturnRequestResponseDTO> requests = returnRequestService.getDepartmentRequests(departmentId);
            return ResponseEntity.ok(
                    ApiResponse.<List<ReturnRequestResponseDTO>>builder()
                            .code(1000)
                            .message("Lấy danh sách yêu cầu trả tài sản theo phòng ban thành công")
                            .result(requests)
                            .build()
            );
        } catch (RuntimeException e) {
            log.error("Error getting department return requests", e);
            return ResponseEntity.badRequest().body(
                    new ApiErrorResponse("GET_DEPARTMENT_REQUESTS_ERROR", e.getMessage())
            );
        }
    }

    /**
     * Lấy danh sách yêu cầu trả tài sản theo dự án
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getProjectRequests(@PathVariable String projectId) {
        try {
            List<ReturnRequestResponseDTO> requests = returnRequestService.getProjectRequests(projectId);
            return ResponseEntity.ok(
                    ApiResponse.<List<ReturnRequestResponseDTO>>builder()
                            .code(1000)
                            .message("Lấy danh sách yêu cầu trả tài sản theo dự án thành công")
                            .result(requests)
                            .build()
            );
        } catch (RuntimeException e) {
            log.error("Error getting project return requests", e);
            return ResponseEntity.badRequest().body(
                    new ApiErrorResponse("GET_PROJECT_REQUESTS_ERROR", e.getMessage())
            );
        }
    }

    /**
     * API thống kê các yêu cầu trả tài sản
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getReturnRequestStatistics() {
        try {
            ReturnRequestStatisticsDTO statistics = returnRequestService.getReturnRequestStatistics();
            return ResponseEntity.ok(
                    ApiResponse.<ReturnRequestStatisticsDTO>builder()
                            .code(1000)
                            .message("Lấy thống kê yêu cầu trả tài sản thành công")
                            .result(statistics)
                            .build()
            );
        } catch (RuntimeException e) {
            log.error("Error getting return request statistics", e);
            return ResponseEntity.badRequest().body(
                    new ApiErrorResponse("GET_STATISTICS_ERROR", e.getMessage())
            );
        }
    }

}