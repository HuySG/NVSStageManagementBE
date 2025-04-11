package com.nvsstagemanagement.nvs_stage_management.service;
import com.nvsstagemanagement.nvs_stage_management.enums.BorrowedAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAsset;
import com.nvsstagemanagement.nvs_stage_management.model.ReturnedAsset;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetUsageHistoryRepository;
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
    private final AssetUsageHistoryRepository assetUsageHistoryRepository;
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

            borrowed.setStatus(BorrowedAssetStatus.RETURNED.name());
            borrowedAssetRepository.save(borrowed);

            assetUsageHistoryRepository.findByAsset_AssetIDAndProject_ProjectID(
                    borrowed.getAsset().getAssetID(),
                    borrowed.getTask().getMilestone().getProject().getProjectID()
            ).ifPresent(usage -> {
                usage.setStatus("Returned");
                assetUsageHistoryRepository.save(usage);
            });

            System.out.println("Auto returned asset: " + borrowed.getAsset().getAssetID());
        }
    }

    @Scheduled(fixedRate = 600000)
    public void autoUpdateBorrowedAssetStatus() {
        Instant now = Instant.now();
        List<BorrowedAsset> bookedAssets = borrowedAssetRepository.findAllByStatus(BorrowedAssetStatus.BOOKED.name());
        for (BorrowedAsset borrowed : bookedAssets) {
            if (borrowed.getBorrowTime() != null && borrowed.getBorrowTime().isBefore(now)) {
                borrowed.setStatus(BorrowedAssetStatus.IN_USE.name());
                borrowedAssetRepository.save(borrowed);
                System.out.println("Auto switched asset to IN_USE: " + borrowed.getAsset().getAssetID());
            }
        }
    }
}
