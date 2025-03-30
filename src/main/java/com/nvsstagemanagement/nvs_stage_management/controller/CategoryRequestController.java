package com.nvsstagemanagement.nvs_stage_management.controller;


import com.nvsstagemanagement.nvs_stage_management.dto.allocation.AllocationDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests/category")
@RequiredArgsConstructor
public class CategoryRequestController {

    private final IAllocationService allocationService;
    @GetMapping("/{requestId}/allocations")
    public ResponseEntity<List<AllocationDTO>> getAllocationDetails(@PathVariable String requestId) {
        List<AllocationDTO> allocationDTOs = allocationService.getAllocationDetails(requestId);
        return ResponseEntity.ok(allocationDTOs);
    }
}
