package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.*;
import com.nvsstagemanagement.nvs_stage_management.enums.BorrowedAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.enums.ReturnRequestStatus;
import com.nvsstagemanagement.nvs_stage_management.enums.NotificationType;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.IReturnRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnRequestService implements IReturnRequestService {
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final ReturnedAssetRepository returnedAssetRepository;
    private final AssetRepository assetRepository;
    private final TaskRepository taskRepository;
    private final NotificationRepository notificationRepository;
    private final AssetUsageHistoryRepository assetUsageHistoryRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RequestAssetRepository requestAssetRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private static final BigDecimal LATE_FEE_PER_DAY = BigDecimal.valueOf(100_000);

    @Override
    @Transactional
    public ReturnRequestResponseDTO createReturnRequest(ReturnRequestDTO dto, String staffId) {
        Asset asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài sản!"));
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhiệm vụ!"));
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên!"));

        validateAssetBorrowing(asset, task, staff);

        ReturnRequest request = new ReturnRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setAsset(asset);
        request.setTask(task);
        request.setStaff(staff);
        request.setDescription(dto.getDescription());
        request.setConditionNote(dto.getConditionNote());
        request.setImageUrl(dto.getImageUrl());
        request.setStatus(ReturnRequestStatus.PENDING);
        request.setRequestTime(LocalDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault()
        ));
        returnRequestRepository.save(request);

        sendNotificationToLeader(request);
        log.info("Return request created: {}", request);
        return convertToResponseDTO(request);
    }

    @Override
    @Transactional
    public ReturnRequestResponseDTO processReturnRequest(
            String requestId,
            ProcessReturnRequestDTO dto,
            String leaderId
    ) {
        ReturnRequest request = returnRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu!"));

        validateRequestProcessing(request);

        request.setProcessedTime(LocalDateTime.now());
        request.setLeader(userRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy leader")));
        request.setLeaderNote(dto.getLeaderNote());
        request.setDamageFee(dto.getDamageFee());

        if (dto.isApproved()) {
            handleApproval(request);
        } else {
            handleRejection(request, dto.getRejectReason());
        }

        returnRequestRepository.save(request);
        sendNotificationToStaff(request);
        return convertToResponseDTO(request);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequestResponseDTO> getStaffRequests(String staffId) {
        return returnRequestRepository.findByStaffId(staffId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequestResponseDTO> getPendingRequests() {
        return returnRequestRepository.findByStatus(ReturnRequestStatus.PENDING).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 3600000)
    public void autoReturnExpiredAssets() {
        Instant now = Instant.now();
        List<BorrowedAsset> overdueAssets = borrowedAssetRepository.findAll().stream()
                .filter(b -> b.getEndTime() != null && b.getEndTime().isBefore(now))
                .filter(b -> !returnedAssetRepository.existsReturnedAssetByAssetID(b.getAsset().getAssetID()))
                .toList();

        overdueAssets.forEach(this::autoReturnAsset);
        log.info("Processed overdue assets: {}", overdueAssets.size());
    }

    private void autoReturnAsset(BorrowedAsset borrowed) {
        ReturnedAsset returned = ReturnedAsset.builder()
                .returnedAssetID(UUID.randomUUID().toString())
                .assetID(borrowed.getAsset())
                .taskID(borrowed.getTask())
                .returnTime(borrowed.getEndTime())
                .description("Trả tự động do quá hạn.")
                .build();
        String userId = borrowed.getTask().getAssignee();
        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        returnedAssetRepository.save(returned);
        Notification notification = Notification.builder()
                .notificationID(UUID.randomUUID().toString())
                .user(receiver)
                .message("Tài sản '" + borrowed.getAsset().getAssetName()
                        + "' đã quá hạn và được tự động trả.")
                .createDate(Instant.now())
                .isRead(false)
                .type(NotificationType.OVERDUE)
                .build();

        notificationRepository.save(notification);

        borrowed.setStatus(BorrowedAssetStatus.RETURNED.name());
        borrowedAssetRepository.save(borrowed);

        updateUsageStatus(borrowed, "Returned");
    }


    private void handleApproval(ReturnRequest request) {
        request.setStatus(ReturnRequestStatus.APPROVED);

        List<String> lookupStatuses = List.of(
                BorrowedAssetStatus.IN_USE.name(),
                BorrowedAssetStatus.OVERDUE.name()
        );

        BorrowedAsset borrowed = borrowedAssetRepository
                .findFirstByAsset_AssetIDAndTask_TaskIDAndStatusInOrderByStartTimeDesc(
                        request.getAsset().getAssetID(),
                        request.getTask().getTaskID(),
                        lookupStatuses
                )
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy bản ghi mượn đang sử dụng hoặc quá hạn cho asset="
                                + request.getAsset().getAssetID()
                                + " & task="
                                + request.getTask().getTaskID()
                ));

        Instant processedInstant = request.getProcessedTime()
                .atZone(ZoneId.systemDefault())
                .toInstant();

        ReturnedAsset returnedAsset = ReturnedAsset.builder()
                .returnedAssetID(UUID.randomUUID().toString())
                .assetID(borrowed.getAsset())
                .taskID(borrowed.getTask())
                .returnTime(processedInstant)
                .actualReturnDate(processedInstant)
                .description(request.getLeaderNote())
                .conditionAfter(request.getConditionNote())
                .imageAfter(request.getImageUrl())
                .latePenaltyFee(BigDecimal.ZERO)
                .build();

        calculateLateFee(returnedAsset, request);
        returnedAssetRepository.save(returnedAsset);

        Asset asset = borrowed.getAsset();
        asset.setStatus("AVAILABLE");
        assetRepository.save(asset);

        borrowed.setStatus(BorrowedAssetStatus.RETURNED.name());
        borrowedAssetRepository.save(borrowed);
        RequestAsset requestAsset = requestAssetRepository
                .findByTask_TaskIDAndAsset_AssetID(
                        request.getTask().getTaskID(),
                        request.getAsset().getAssetID()
                )
                .orElse(null);
        if (requestAsset != null) {
            requestAsset.setStatus(ReturnRequestStatus.APPROVED.name());
            requestAssetRepository.save(requestAsset);
        }
        updateUsageStatus(borrowed, "Returned");
    }



    private void handleRejection(ReturnRequest request, String rejectReason) {
        request.setStatus(ReturnRequestStatus.REJECTED);

        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            throw new RuntimeException("Cần nêu rõ lý do từ chối");
        }

        request.setRejectReason(rejectReason);
    }

    private void calculateLateFee(ReturnedAsset returnedAsset, ReturnRequest request) {
        LocalDate expectedDate = request.getTask().getEndDate();
        Instant expectedReturn = expectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
        Instant now = Instant.now();

        if (now.isAfter(expectedReturn)) {
            long lateDays = Duration.between(expectedReturn, now).toDays();
            if (lateDays > 0) {
                returnedAsset.setLatePenaltyFee(LATE_FEE_PER_DAY.multiply(BigDecimal.valueOf(lateDays)));
            }
        }
    }

    private void updateUsageStatus(BorrowedAsset borrowed, String status) {
        List<AssetUsageHistory> histories = assetUsageHistoryRepository
                .findByAsset_AssetIDAndProject_ProjectID(
                        borrowed.getAsset().getAssetID(),
                        borrowed.getTask().getMilestone().getProject().getProjectID()
                );
        if (histories.isEmpty()) {
            log.warn("No usage‐history found for asset={} project={}",
                    borrowed.getAsset().getAssetID(),
                    borrowed.getTask().getMilestone().getProject().getProjectID());
            return;
        }
        histories.forEach(h -> h.setStatus(status));
        assetUsageHistoryRepository.saveAll(histories);
    }


    private ReturnRequestResponseDTO convertToResponseDTO(ReturnRequest request) {
        ReturnRequestResponseDTO dto = new ReturnRequestResponseDTO();
        dto.setRequestId(request.getRequestId());
        dto.setAssetId(request.getAsset().getAssetID());
        dto.setTaskId(request.getTask().getTaskID());
        dto.setStaffId(request.getStaff().getId());
        dto.setDescription(request.getDescription());
        dto.setConditionNote(request.getConditionNote());
        dto.setImageUrl(request.getImageUrl());
        dto.setStatus(request.getStatus());
        dto.setRequestTime(request.getRequestTime());
        dto.setProcessedTime(request.getProcessedTime());
        dto.setRejectReason(request.getRejectReason());
        return dto;
    }

    private void sendNotificationToLeader(ReturnRequest request) {
        String departmentId = request.getStaff().getDepartment().getDepartmentId();
        String projectId = Optional.ofNullable(request.getTask())
                .map(Task::getMilestone)
                .map(Milestone::getProject)
                .map(Project::getProjectID)
                .orElseThrow(() -> new RuntimeException("Không có thông tin project"));
        List<User> leaders = userRepository
                .findLeadersByDepartmentAndProject(departmentId, projectId);

        if (leaders.isEmpty()) {
            log.warn("Không tìm thấy Leader (roleId=4) cho phòng ban {}", departmentId);
            return;
        }
        String assetName      = request.getAsset().getAssetName();
        String staffName      = request.getStaff().getFullName();
        String taskTitle      = request.getTask().getTitle();
        String projectName    = Optional.ofNullable(request.getTask().getMilestone())
                .map(m -> m.getProject().getTitle())
                .orElse("Không có dự án");
        String departmentName = request.getStaff().getDepartment().getName();
        for (User leader : leaders) {
            String message = String.format(
                    "Yêu cầu trả tài sản '%s' cho task '%s' thuộc dự án '%s' từ nhân viên %s (Phòng ban: %s) cần được xác nhận",
                    assetName, taskTitle, projectName, staffName, departmentName
            );

            Notification notification = Notification.builder()
                    .notificationID(UUID.randomUUID().toString())
                    .user(leader)
                    .message(message)
                    .createDate(Instant.now())
                    .type(NotificationType.RETURN_REQUEST)
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);

            log.info("Đã gửi thông báo RETURN_REQUEST cho Leader={} (dept={}) về ReturnRequest={}",
                    leader.getId(), departmentId, request.getRequestId());
        }
    }


private void sendNotificationToStaff(ReturnRequest request) {
    String assetName = request.getAsset().getAssetName();
    String taskTitle = request.getTask().getTitle();
    String projectName = request.getTask().getMilestone().getProject().getTitle();
    
    String message;
    NotificationType type;
    if (ReturnRequestStatus.APPROVED.equals(request.getStatus())) {
        message = String.format(
            "Yêu cầu trả tài sản '%s' cho task '%s' thuộc dự án '%s' đã được chấp nhận. %s%s",
            assetName, taskTitle, projectName,
            request.getLeaderNote() != null ? "\nGhi chú: " + request.getLeaderNote() : "",
            request.getDamageFee() != null && request.getDamageFee().compareTo(BigDecimal.ZERO) > 0 ? 
                "\nPhí hư hỏng: " + request.getDamageFee() + " VND" : ""
        );
        type = NotificationType.RETURN_APPROVED;
    } else {
        message = String.format(
            "Yêu cầu trả tài sản '%s' cho task '%s' thuộc dự án '%s' đã bị từ chối.\nLý do: %s",
            assetName, taskTitle, projectName,
            request.getRejectReason()
        );
        type = NotificationType.RETURN_REJECTED;
    }
    Notification notification = Notification.builder()
            .notificationID(UUID.randomUUID().toString())
            .user(request.getStaff())
            .message(message)
            .createDate(Instant.now())
            .isRead(false)
            .type(type)
            .build();

    notificationRepository.save(notification);
    log.info("Đã gửi thông báo cho nhân viên {} về kết quả yêu cầu trả tài sản {}", 
             request.getStaff().getId(), request.getRequestId());
}

private void validateAssetBorrowing(Asset asset, Task task, User staff) {
    BorrowedAsset borrowedAsset = borrowedAssetRepository
            .findByAsset_AssetIDAndTask_TaskID(asset.getAssetID(), task.getTaskID())
            .orElseThrow(() -> new RuntimeException(
                String.format("Tài sản '%s' không được mượn cho task '%s'",
                            asset.getAssetName(), task.getTitle())
            ));

    if (!task.getAssignee().equals(staff.getId())) {
        throw new RuntimeException(
            String.format("Bạn không được phân công cho task '%s'. Task này được gán cho người khác.",
                        task.getTitle())
        );
    }
    String status = borrowedAsset.getStatus();
    if (!(BorrowedAssetStatus.IN_USE.name().equals(status)
            || BorrowedAssetStatus.OVERDUE.name().equals(status))) {
        throw new RuntimeException(
                String.format("Tài sản '%s' không ở trạng thái đang sử dụng hoặc quá hạn (current status: %s)",
                        asset.getAssetName(), status)
        );
    }
    boolean hasPendingRequest = returnRequestRepository
            .findByAsset_AssetIDAndTask_TaskIDAndStatus(
                asset.getAssetID(),
                task.getTaskID(),
                ReturnRequestStatus.PENDING
            )
            .isPresent();

    if (hasPendingRequest) {
        throw new RuntimeException(
            String.format("Đã tồn tại yêu cầu trả tài sản '%s' đang chờ xử lý cho task '%s'",
                        asset.getAssetName(), task.getTitle())
        );
    }

}

    private void validateRequestProcessing(ReturnRequest request) {
        if (!ReturnRequestStatus.PENDING.equals(request.getStatus())) {
            throw new RuntimeException("Yêu cầu trả tài sản đã được xử lý.");
        }
    }
    @Override
    @Transactional(readOnly = true)
    public ReturnRequestResponseDTO getReturnRequestById(String requestId) {
        ReturnRequest request = returnRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Return request not found with id: " + requestId));
        return modelMapper.map(request, ReturnRequestResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequestResponseDTO> getDepartmentRequests(String departmentId) {

        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        List<ReturnRequest> requests =
                returnRequestRepository.findByStaff_Department_DepartmentId(departmentId);

        return requests.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequestResponseDTO> getProjectRequests(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<ReturnRequest> requests = returnRequestRepository.findByTask_Milestone_Project_ProjectID(projectId);
        return requests.stream()
                .map(request -> modelMapper.map(request, ReturnRequestResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnRequestStatisticsDTO getReturnRequestStatistics() {
        List<ReturnRequest> allRequests = returnRequestRepository.findAll();
        ReturnRequestStatisticsDTO statistics = ReturnRequestStatisticsDTO.builder()
                .totalRequests(allRequests.size())
                .pendingRequests(countRequestsByStatus(allRequests, "PENDING"))
                .approvedRequests(countRequestsByStatus(allRequests, "APPROVED"))
                .rejectedRequests(countRequestsByStatus(allRequests, "REJECTED"))
                .lateReturns(countLateReturns(allRequests))
                .totalLateFees(calculateTotalLateFees(allRequests))
                .totalDamageFees(calculateTotalDamageFees(allRequests))
                .build();

        statistics.setDepartmentStatistics(calculateDepartmentStatistics(allRequests));
        statistics.setProjectStatistics(calculateProjectStatistics(allRequests));

        return statistics;
    }

    private long countRequestsByStatus(List<ReturnRequest> requests, String status) {
        return requests.stream()
                .filter(r -> r.getStatus().equals(status))
                .count();
    }

    private long countLateReturns(List<ReturnRequest> requests) {
        return requests.stream()
                .filter(r -> r.getProcessedTime() != null
                        && r.getProcessedTime().isAfter(r.getTask().getEndDate().atStartOfDay()))
                .count();
    }

    private BigDecimal calculateTotalLateFees(List<ReturnRequest> requests) {
        return requests.stream()
                .map(ReturnRequest::getLateFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalDamageFees(List<ReturnRequest> requests) {
        return requests.stream()
                .map(ReturnRequest::getDamageFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<ReturnRequestStatisticsDTO.DepartmentStatistics> calculateDepartmentStatistics(
            List<ReturnRequest> allRequests) {

        Map<String, List<ReturnRequest>> requestsByDepartment = allRequests.stream()
                .filter(r -> r.getStaff() != null && r.getStaff().getDepartment() != null)
                .collect(Collectors.groupingBy(r -> r.getStaff().getDepartment().getDepartmentId()));

        return requestsByDepartment.entrySet().stream()
                .map(entry -> {
                    String deptId = entry.getKey();
                    Department dept = departmentRepository.findById(deptId)
                            .orElseThrow(() -> new RuntimeException("Department not found: " + deptId));
                    List<ReturnRequest> deptRequests = entry.getValue();

                    long pending  = countRequestsByStatus(deptRequests, ReturnRequestStatus.PENDING.name());
                    long approved = countRequestsByStatus(deptRequests, ReturnRequestStatus.APPROVED.name());
                    long rejected = countRequestsByStatus(deptRequests, ReturnRequestStatus.REJECTED.name());
                    BigDecimal totalFees = calculateTotalFees(deptRequests);

                    return ReturnRequestStatisticsDTO.DepartmentStatistics.builder()
                            .departmentId(dept.getDepartmentId())
                            .departmentName(dept.getName())
                            .totalRequests(deptRequests.size())
                            .pendingRequests(pending)
                            .approvedRequests(approved)
                            .rejectedRequests(rejected)
                            .totalFees(totalFees)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<ReturnRequestStatisticsDTO.ProjectStatistics> calculateProjectStatistics(
            List<ReturnRequest> allRequests) {
        Map<String, List<ReturnRequest>> requestsByProject = allRequests.stream()
                .filter(r -> r.getTask() != null
                        && r.getTask().getMilestone() != null
                        && r.getTask().getMilestone().getProject() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getTask()
                                .getMilestone()
                                .getProject()
                                .getProjectID()
                ));
        return requestsByProject.entrySet().stream()
                .map(entry -> {
                    String projectId = entry.getKey();
                    Project project = projectRepository.findById(projectId)
                            .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));
                    List<ReturnRequest> projectRequests = entry.getValue();

                    long pending  = countRequestsByStatus(projectRequests, ReturnRequestStatus.PENDING.name());
                    long approved = countRequestsByStatus(projectRequests, ReturnRequestStatus.APPROVED.name());
                    long rejected = countRequestsByStatus(projectRequests, ReturnRequestStatus.REJECTED.name());
                    BigDecimal totalFees = calculateTotalFees(projectRequests);

                    return ReturnRequestStatisticsDTO.ProjectStatistics.builder()
                            .projectId(project.getProjectID())
                            .projectName(project.getTitle())
                            .totalRequests(projectRequests.size())
                            .pendingRequests(pending)
                            .approvedRequests(approved)
                            .rejectedRequests(rejected)
                            .totalFees(totalFees)
                            .build();
                })
                .collect(Collectors.toList());
    }


    private BigDecimal calculateTotalFees(List<ReturnRequest> requests) {
        BigDecimal lateFees = calculateTotalLateFees(requests);
        BigDecimal damageFees = calculateTotalDamageFees(requests);
        return lateFees.add(damageFees);
    }

}