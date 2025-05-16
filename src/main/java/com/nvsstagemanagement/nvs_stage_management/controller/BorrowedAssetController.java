package com.nvsstagemanagement.nvs_stage_management.controller;


import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetsOverviewDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffBorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.request.ApiResponse;
import com.nvsstagemanagement.nvs_stage_management.exception.ApiErrorResponse;
import com.nvsstagemanagement.nvs_stage_management.service.IBorrowedAssetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borrowed-assets")
@RequiredArgsConstructor
@Slf4j
public class BorrowedAssetController {


    private final IBorrowedAssetService borrowedAssetService;

    @PostMapping("/create")
    public ResponseEntity<BorrowedAssetDTO> createBorrowedAsset(@RequestBody BorrowedAssetDTO borrowedAssetDTO) {
        BorrowedAssetDTO savedAsset = borrowedAssetService.createBorrowedAsset(borrowedAssetDTO);
        return ResponseEntity.ok(savedAsset);
    }

    @GetMapping
    public ResponseEntity<List<BorrowedAssetDTO>> getAllBorrowedAssets() {
        return ResponseEntity.ok(borrowedAssetService.getAllBorrowedAssets());
    }

    @GetMapping("/borrowedId")
    public ResponseEntity<BorrowedAssetDTO> getBorrowedAssetById(@RequestParam String borrowedId) {
        return borrowedAssetService.getBorrowedAssetById(borrowedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/borrowedId")
    public ResponseEntity<Void> deleteBorrowedAsset(@PathVariable String borrowedId) {
        borrowedAssetService.deleteBorrowedAsset(borrowedId);
        return ResponseEntity.noContent().build();
    }
    /**
     * API lấy tổng quan tài sản đang được mượn:
     * - Tổng số tài sản đang được mượn
     * - Danh sách project + số department đang sử dụng + số tài sản đang mượn.
     *
     * @return BorrowedAssetsOverviewDTO bao gồm tổng số tài sản mượn và danh sách dự án
     */
    @GetMapping("/overview")
    public ResponseEntity<BorrowedAssetsOverviewDTO> getBorrowedAssetsOverview() {
        BorrowedAssetsOverviewDTO overview = borrowedAssetService.getBorrowedAssetsOverview();
        return ResponseEntity.ok(overview);
    }
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<?> getBorrowedAssetsByStaff(@PathVariable String staffId) {
        try {
            List<StaffBorrowedAssetDTO> list = borrowedAssetService.getBorrowedAssetsByStaff(staffId);
            return ResponseEntity.ok(
                    ApiResponse.<List<StaffBorrowedAssetDTO>>builder()
                            .code(1000)
                            .message("Lấy danh sách tài sản mượn thành công")
                            .result(list)
                            .build()
            );
        } catch (RuntimeException e) {
            log.error("Error fetching borrowed assets for staffId={}", staffId, e);
            return ResponseEntity.badRequest()
                    .body(new ApiErrorResponse("GET_BORROWED_ASSETS_ERROR", e.getMessage()));
        }
    }


}
