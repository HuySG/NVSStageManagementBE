package com.nvsstagemanagement.nvs_stage_management.service;
import com.nvsstagemanagement.nvs_stage_management.enums.*;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledReturnService {

    private final BorrowedAssetRepository borrowedAssetRepository;
    private final ReturnedAssetRepository returnedAssetRepository;
    private final AssetUsageHistoryRepository assetUsageHistoryRepository;
    private final RequestAssetRepository requestAssetRepository;
    private final NotificationRepository notificationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    @Scheduled(fixedRate = 3600000)
    public void autoReturnExpiredBookingAssets() {
        Instant now = Instant.now();

        List<BorrowedAsset> overdueAssets = borrowedAssetRepository.findAll()
                .stream()
                .filter(b -> b.getEndTime() != null && b.getEndTime().isBefore(now))
                .filter(b -> !returnedAssetRepository.existsReturnedAssetByAssetID(b.getAsset().getAssetID()))
                .filter(b -> {
                    Task task = b.getTask();
                    if (task == null) return false;
                    List<RequestAsset> requestAssets = requestAssetRepository.findByTask(task);
                    return requestAssets.stream()
                            .anyMatch(r -> r.getBookingType() != null &&
                                    (r.getBookingType().name().equals("ONE_TIME") || r.getBookingType().name().equals("RECURRING")));
                })
                .toList();

        for (BorrowedAsset borrowed : overdueAssets) {
            ReturnedAsset returnedAsset = new ReturnedAsset();
            returnedAsset.setReturnedAssetID(UUID.randomUUID().toString());
            returnedAsset.setAssetID(borrowed.getAsset());
            returnedAsset.setTaskID(borrowed.getTask());
            returnedAsset.setReturnTime(borrowed.getEndTime());
            returnedAsset.setDescription("Auto return after overdue.");
            returnedAssetRepository.save(returnedAsset);
            Notification notification = new Notification();
            notification.setNotificationID(UUID.randomUUID().toString());
            notification.setCreateDate(Instant.now());
            notification.setMessage("Phòng'" + borrowed.getAsset().getAssetName() + "' đã quá hạn và được tự động trả.");
            notification.setType(NotificationType.OVERDUE);
            userRepository.findById(borrowed.getTask().getAssignee()).ifPresent(notification::setUser);
            notificationRepository.save(notification);
            borrowed.setStatus(BorrowedAssetStatus.RETURNED.name());
            borrowedAssetRepository.save(borrowed);

            assetUsageHistoryRepository.findByAsset_AssetIDAndProject_ProjectID(
                    borrowed.getAsset().getAssetID(),
                    borrowed.getTask().getMilestone().getProject().getProjectID()
            ).ifPresent(usage -> {
                usage.setStatus("Returned");
                assetUsageHistoryRepository.save(usage);
            });

            System.out.println("✅ Auto returned asset: " + borrowed.getAsset().getAssetID());
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
    @Scheduled(fixedRate = 3600000)
    public void autoCancelOverduePendingRequests() {
        Instant now = Instant.now();
        List<RequestAsset> overdueRequests = requestAssetRepository.findAll()
                .stream()
                .filter(r -> r.getStatus().equals(RequestAssetStatus.PENDING_LEADER.name()) ||
                        r.getStatus().equals(RequestAssetStatus.PENDING_AM.name()))
                .filter(r -> r.getEndTime() != null && r.getEndTime().isBefore(now))
                .toList();

        for (RequestAsset request : overdueRequests) {
            request.setStatus(RequestAssetStatus.CANCELLED.name());
            Notification notification = new Notification();
            notification.setNotificationID(UUID.randomUUID().toString());
            notification.setCreateDate(Instant.now());
            notification.setMessage("Yêu cầu mượn tài sản '" + request.getTitle() + "' đã bị hủy do quá hạn xử lý.");
            notification.setType(NotificationType.AUTO_CANCELLED);
            userRepository.findById(request.getCreateBy()).ifPresent(notification::setUser);
            notificationRepository.save(notification);
            requestAssetRepository.save(request);
            System.out.println("Auto cancelled request: " + request.getRequestId());
        }
    }
//    @Scheduled(fixedRate = 60000)
//    public void autoUpdateProjectStatus() {
//        List<Project> allProjects = projectRepository.findAllWithMilestonesAndTasks();
//
//        for (Project project : allProjects) {
//            if (project.getStatus() == ProjectStatus.NEW &&
//                    project.getMilestones() != null &&
//                    !project.getMilestones().isEmpty()) {
//
//                project.setStatus(ProjectStatus.IN_PROGRESS);
//                projectRepository.save(project);
//
//                System.out.println("✅ Updated project to IN_PROGRESS: " + project.getProjectID());
//            }
//        }
//    }
    @Scheduled(fixedRate = 60 * 60 * 1000)  // 1 giờ
    public void autoMarkOverdueBorrowedAssets() {
        Instant now = Instant.now();
        borrowedAssetRepository.findAllByStatus(BorrowedAssetStatus.IN_USE.name()).stream()
                .filter(b -> b.getEndTime() != null && b.getEndTime().isBefore(now))
                .forEach(borrowed -> {
                    borrowed.setStatus(BorrowedAssetStatus.OVERDUE.name());
                    borrowedAssetRepository.save(borrowed);
                    Notification notif = new Notification();
                    notif.setNotificationID(UUID.randomUUID().toString());
                    notif.setCreateDate(Instant.now());
                    notif.setMessage("Tài sản '" + borrowed.getAsset().getAssetName()
                            + "' của bạn đã quá hạn mượn. Vui lòng trả lại ban quản lý tài sản.");
                    notif.setType(NotificationType.OVERDUE);
                    userRepository.findById(borrowed.getTask().getAssignee())
                            .ifPresent(notif::setUser);
                    notificationRepository.save(notif);

                    log.info("Marked overdue: borrowedID={}, assetId={}",
                            borrowed.getBorrowedID(), borrowed.getAsset().getAssetID());
                });
    }
    @Scheduled(fixedRate = 60_000)
    public void autoStartBorrowedAssets() {
        Instant now = Instant.now();
        List<BorrowedAsset> preparingList = borrowedAssetRepository
                .findAllByStatus(BorrowedAssetStatus.PREPARING.name());

        for (BorrowedAsset ba : preparingList) {
            Instant start = ba.getStartTime();
            if (start != null && !start.isAfter(now)) {
                ba.setStatus(BorrowedAssetStatus.IN_USE.name());
                borrowedAssetRepository.save(ba);
                log.info("Auto-updated BorrowedAsset {} → IN_USE (startTime was {})",
                        ba.getBorrowedID(), start);
            }
        }
    }
}
