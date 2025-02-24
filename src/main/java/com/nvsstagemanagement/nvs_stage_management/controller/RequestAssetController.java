package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.CreateRequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IRequestAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/requestAsset")
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
    @GetMapping("/{id}")
    public ResponseEntity<RequestAssetDTO> getRequestById(@PathVariable String id){
        RequestAssetDTO requestAssetDTO = requestAssetService.getRequestById(id);
        return new ResponseEntity<>(requestAssetDTO, HttpStatus.OK);
    }
}
