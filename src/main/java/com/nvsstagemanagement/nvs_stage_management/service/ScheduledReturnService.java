package com.nvsstagemanagement.nvs_stage_management.service;
import com.nvsstagemanagement.nvs_stage_management.enums.*;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            List<AssetUsageHistory> usages = assetUsageHistoryRepository
                    .findByAsset_AssetIDAndProject_ProjectID(
                            borrowed.getAsset().getAssetID(),
                            borrowed.getTask().getMilestone().getProject().getProjectID()
                    );
            if (!usages.isEmpty()) {
                usages.forEach(u -> u.setStatus("Returned"));
                assetUsageHistoryRepository.saveAll(usages);
            } else {
                log.warn("No usage history found for asset={} project={}",
                        borrowed.getAsset().getAssetID(),
                        borrowed.getTask().getMilestone().getProject().getProjectID());
            }
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
    /**
     * Mỗi ngày 1:00 AM, tự động tạo thêm booking mới theo các pattern RECURRING.
     * - WEEKLY: nếu hôm nay là selectedDay và vẫn trong recurrenceEndDate.
     * - MONTHLY: nếu hôm nay là dayOfMonth (hoặc cuối tháng nếu fallback).
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void generateDailyRecurringSlots() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        Instant nowInstant = Instant.now();
        List<RequestAsset> patterns = requestAssetRepository
                .findByBookingTypeAndRecurrenceTypeInAndRecurrenceEndDateAfter(
                        BookingType.RECURRING,
                        List.of(RecurrenceType.WEEKLY, RecurrenceType.MONTHLY),
                        LocalDate.now()
                );

        patterns.addAll(requestAssetRepository
                .findByBookingTypeAndRecurrenceTypeAndRecurrenceEndDateAfter(
                        BookingType.RECURRING, RecurrenceType.MONTHLY, today.minusDays(1)));

        for (RequestAsset pattern : patterns) {
            RecurrenceType type = pattern.getRecurrenceType();
            LocalTime baseStartTime = pattern.getStartTime()
                    .atZone(ZoneId.systemDefault()).toLocalTime();
            Duration duration = Duration.between(
                    pattern.getStartTime().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    pattern.getEndTime().  atZone(ZoneId.systemDefault()).toLocalDateTime()
            );

            boolean shouldGenerate = false;
            if (type == RecurrenceType.WEEKLY) {
                if (pattern.getSelectedDaysOfWeek().contains(today.getDayOfWeek())) {
                    shouldGenerate = true;
                }
            } else if (type == RecurrenceType.MONTHLY) {
                int dom = pattern.getDayOfMonth();
                YearMonth ym = YearMonth.from(today);
                int actualDay = Math.min(dom, ym.lengthOfMonth());
                if (today.getDayOfMonth() == actualDay) {
                    shouldGenerate = true;
                }
            }

            if (!shouldGenerate) continue;
            boolean exists = requestAssetRepository.existsByAsset_AssetIDAndStartTimeBetween(
                    pattern.getAsset().getAssetID(),
                    today.atTime(baseStartTime).atZone(ZoneId.systemDefault()).toInstant(),
                    today.atTime(baseStartTime).atZone(ZoneId.systemDefault()).toInstant().plus(duration)
            );
            if (exists) {
                continue;
            }
            Instant slotStart = today.atTime(baseStartTime)
                    .atZone(ZoneId.systemDefault()).toInstant();
            Instant slotEnd   = slotStart.plus(duration);

            RequestAsset slot = new RequestAsset();
            slot.setRequestId(UUID.randomUUID().toString());
            slot.setTitle(pattern.getTitle());
            slot.setDescription(pattern.getDescription());
            slot.setAsset(pattern.getAsset());
            slot.setTask(pattern.getTask());
            slot.setCreateBy(pattern.getCreateBy());
            slot.setStartTime(slotStart);
            slot.setEndTime(slotEnd);
            slot.setStatus(RequestAssetStatus.BOOKED.name());
            slot.setBookingType(pattern.getBookingType());
            slot.setRecurrenceType(pattern.getRecurrenceType());
            slot.setRecurrenceInterval(pattern.getRecurrenceInterval());
            slot.setRecurrenceEndDate(pattern.getRecurrenceEndDate());
            slot.setSelectedDaysOfWeek(pattern.getSelectedDaysOfWeek());
            slot.setDayOfMonth(pattern.getDayOfMonth());
            slot.setFallbackToLastDay(pattern.getFallbackToLastDay());
            slot.setRecurrenceCount(pattern.getRecurrenceCount());

            requestAssetRepository.save(slot);
            log.info("➕ Generated recurring booking slot {} for pattern {} on {}",
                    slot.getRequestId(), pattern.getRequestId(), today);
        }
    }
}
