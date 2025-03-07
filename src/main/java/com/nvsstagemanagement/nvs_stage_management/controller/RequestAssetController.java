package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.CreateRequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.UpdateRequestAssetStatusDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IRequestAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/request-asset")
@RequiredArgsConstructor
public class RequestAssetController {
    private final IRequestAssetService requestAssetService;
    @GetMapping
    public List<RequestAssetDTO> getAllAssets() {
        return requestAssetService.getAllRequest();
    }
    @PostMapping
    public ResponseEntity<RequestAssetDTO> createRequestAsset(@RequestBody CreateRequestAssetDTO createRequestAssetDTO){
        RequestAssetDTO createRequest = requestAssetService.createRequest(createRequestAssetDTO);
        return new ResponseEntity<>(createRequest, HttpStatus.CREATED);
    }
    @GetMapping("/requestId")
    public ResponseEntity<RequestAssetDTO> getRequestById(@RequestParam String requestId){
        RequestAssetDTO requestAssetDTO = requestAssetService.getRequestById(requestId);
        return new ResponseEntity<>(requestAssetDTO, HttpStatus.OK);
    }
    @PutMapping("/status")
    public ResponseEntity<RequestAssetDTO> updateRequestAssetStatus(@RequestBody UpdateRequestAssetStatusDTO dto) {
        RequestAssetDTO response = requestAssetService.updateRequestAssetStatus(dto);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/leader/departmentId")
    public ResponseEntity<List<RequestAssetDTO>> getRequestsForLeader(
            @RequestParam String departmentId,
            @RequestParam(defaultValue = "PENDING_LEADER") String status) {
        List<RequestAssetDTO> dtos = requestAssetService.getRequestsForLeader(departmentId, status);
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/user")
    public ResponseEntity<List<RequestAssetDTO>> getMyRequests(@RequestParam String userId) {
        List<RequestAssetDTO> requests = requestAssetService.getRequestsByUser(userId);
        return ResponseEntity.ok(requests);
    }
    @GetMapping("/asset-manager")
    public ResponseEntity<List<RequestAssetDTO>> getRequestsForAssetManager() {
        List<RequestAssetDTO> requests = requestAssetService.getRequestsForAssetManager();
        return ResponseEntity.ok(requests);
    }
}
