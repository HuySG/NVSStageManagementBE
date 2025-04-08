package com.nvsstagemanagement.nvs_stage_management.service;
import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAsset;
import com.nvsstagemanagement.nvs_stage_management.model.ReturnedAsset;
import com.nvsstagemanagement.nvs_stage_management.repository.BorrowedAssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.ReturnedAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduledReturnService {

    private final BorrowedAssetRepository borrowedAssetRepository;
    private final ReturnedAssetRepository returnedAssetRepository;

    @Scheduled(fixedRate = 3600000)
    public void autoReturnExpiredAssets() {
        Instant now = Instant.now();

        List<BorrowedAsset> overdueAssets = borrowedAssetRepository.findAll()
                .stream()
                .filter(b -> b.getEndTime() != null && b.getEndTime().isBefore(now))
                .filter(b -> !returnedAssetRepository.existsReturnedAssetByAssetID(b.getAsset().getAssetID()))
                .toList();

        for (BorrowedAsset borrowed : overdueAssets) {
            ReturnedAsset returnedAsset = new ReturnedAsset();
            returnedAsset.setReturnedAssetID(UUID.randomUUID().toString());
            returnedAsset.setAssetID(borrowed.getAsset());
            returnedAsset.setTaskID(borrowed.getTask());
            returnedAsset.setReturnTime(borrowed.getEndTime());
            returnedAsset.setDescription("Auto return after overdue.");

            returnedAssetRepository.save(returnedAsset);

            System.out.println("✅ Auto returned asset: " + borrowed.getAsset().getAssetID());
        }
    }
}
