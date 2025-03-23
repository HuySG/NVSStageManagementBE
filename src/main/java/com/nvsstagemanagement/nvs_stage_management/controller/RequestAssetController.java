package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.exception.NotEnoughAssetException;
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
    public ResponseEntity<List<RequestAssetDTO>> createRequests(@RequestBody List<CreateRequestAssetDTO> dtos) {
        List<RequestAssetDTO> responses = requestAssetService.createRequest(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
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
    @GetMapping("/leader/department")
    public ResponseEntity<?> getRequestsForLeader(@RequestParam String Id) {
        try {
            List<RequestAssetDTO> dtos = requestAssetService.getRequestsForLeader(Id);
            return ResponseEntity.ok(dtos);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving requests: " + ex.getMessage());
        }
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
    @PutMapping("/accept")
    public ResponseEntity<?> acceptRequest(@RequestParam String requestId) {
        try {
            RequestAssetDTO updatedRequest = requestAssetService.acceptRequest(requestId);
            return ResponseEntity.ok(updatedRequest);
        } catch (NotEnoughAssetException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
