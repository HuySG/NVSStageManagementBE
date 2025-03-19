package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAsset;
import com.nvsstagemanagement.nvs_stage_management.model.Task;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.BorrowedAssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IBorrowedAssetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BorrowedAssetService implements IBorrowedAssetService {


    private final BorrowedAssetRepository borrowedAssetRepository;
    private final AssetRepository assetRepository;
    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;

    @Override
    public BorrowedAssetDTO createBorrowedAsset(BorrowedAssetDTO dto) {
        BorrowedAsset borrowedAsset = new BorrowedAsset();

        borrowedAsset.setBorrowedID(UUID.randomUUID().toString());

        borrowedAsset.setBorrowTime(dto.getBorrowTime());
//        borrowedAsset.setQuantity(dto.getQuantity());
        borrowedAsset.setDescription(dto.getDescription());

        Asset asset = assetRepository.findById(dto.getAssetID())
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        Task task = taskRepository.findById(dto.getTaskID())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        borrowedAsset.setAsset(asset);
        borrowedAsset.setTask(task);


        borrowedAssetRepository.save(borrowedAsset);

        return modelMapper.map(borrowedAsset, BorrowedAssetDTO.class);
    }

    @Override
    public List<BorrowedAssetDTO> getAllBorrowedAssets() {
        return borrowedAssetRepository.findAll()
                .stream()
                .map(asset -> modelMapper.map(asset, BorrowedAssetDTO.class)).toList();
    }

    @Override
    public Optional<BorrowedAssetDTO> getBorrowedAssetById(String borrowedId) {
        return borrowedAssetRepository.findById(borrowedId)
                .map(asset -> modelMapper.map(asset, BorrowedAssetDTO.class));
    }


    @Override
    public void deleteBorrowedAsset(String borrowedId) {
        borrowedAssetRepository.deleteById(borrowedId);
    }

}
