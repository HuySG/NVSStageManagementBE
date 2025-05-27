package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.exception.NotEnoughAssetException;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.*;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.*;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.IRequestAssetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestAssetService implements IRequestAssetService {
    private final RequestAssetRepository requestAssetRepository;
    private final AssetRepository assetRepository;
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final ProjectAssetPermissionRepository projectAssetPermissionRepository;
    private final AssetUsageHistoryRepository assetUsageHistoryRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ReturnedAssetRepository returnedAssetRepository;
    private final RequestAssetAllocationRepository requestAssetAllocationRepository;
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<RequestAssetDTO> getAllRequest() {
        List<RequestAsset> requests = requestAssetRepository.findAll();
        return requests.stream()
                .map(request -> modelMapper.map(request, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestAssetDTO> createRequest(List<CreateRequestAssetDTO> dtos) {
        List<RequestAssetDTO> responses = new ArrayList<>();
        for (CreateRequestAssetDTO dto : dtos) {
            RequestAsset requestAsset = new RequestAsset();
            requestAsset.setRequestId(UUID.randomUUID().toString());
            requestAsset.setTitle(dto.getTitle());
            requestAsset.setDescription(dto.getDescription());
            requestAsset.setStartTime(dto.getStartTime());
            requestAsset.setEndTime(dto.getEndTime());
            requestAsset.setRequestTime(Instant.now());
            requestAsset.setExpectedReturnDate(dto.getExpectedReturnDate());

            requestAsset.setStatus(RequestAssetStatus.PENDING_LEADER.toString());
            if (dto.getTaskID() != null && !dto.getTaskID().isEmpty()) {
                Task task = taskRepository.findById(dto.getTaskID())
                        .orElseThrow(() -> new RuntimeException("Task not found: " + dto.getTaskID()));
                requestAsset.setTask(task);
                if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
                    User requester = userRepository.findById(task.getAssignee())
                            .orElseThrow(
                                    () -> new RuntimeException("Requester not found with ID: " + task.getAssignee()));
                    requestAsset.setCreateBy(requester.getId());
                }
            }
            if (dto.getAssetID() != null && !dto.getAssetID().isEmpty()) {
                Asset asset = assetRepository.findById(dto.getAssetID())
                        .orElseThrow(() -> new RuntimeException("Asset not found: " + dto.getAssetID()));
                requestAsset.setAsset(asset);
            } else if (dto.getCategoryID() != null && !dto.getCategoryID().isEmpty()) {
                Category category = categoryRepository.findById(dto.getCategoryID())
                        .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryID()));
                if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be > 0 for category-based requests.");
                }
                RequestAssetCategory rac = new RequestAssetCategory();
                rac.setRequestAsset(requestAsset);
                rac.setCategory(category);
                rac.setQuantity(dto.getQuantity());
                RequestAssetCategoryId racId = new RequestAssetCategoryId();
                racId.setRequestId(requestAsset.getRequestId());
                racId.setCategoryId(category.getCategoryID());
                rac.setId(racId);
                if (requestAsset.getRequestAssetCategories() == null) {
                    requestAsset.setRequestAssetCategories(new ArrayList<>());
                }
                requestAsset.getRequestAssetCategories().add(rac);
            } else {
                throw new RuntimeException("Either assetID or categoryID must be provided.");
            }
            RequestAsset savedRequest = requestAssetRepository.save(requestAsset);
            RequestAssetDTO responseDto = modelMapper.map(savedRequest, RequestAssetDTO.class);
            if (savedRequest.getCreateBy() != null) {
                User requester = userRepository.findById(savedRequest.getCreateBy())
                        .orElse(null);
                if (requester != null) {
                    UserDTO requesterDTO = modelMapper.map(requester, UserDTO.class);
                    responseDto.setRequesterInfo(requesterDTO);
                }
            }
            responses.add(responseDto);
        }
        return responses;
    }

    @Override
    public RequestAssetDTO getRequestById(String id) {
        RequestAsset requestAsset = requestAssetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: " + id));
        return modelMapper.map(requestAsset, RequestAssetDTO.class);
    }

    @Override
    public List<RequestAssetDTO> getRequestsByAssetId(String assetId) {
        List<RequestAsset> requests = requestAssetRepository.findByAssetID(assetId);
        return requests.stream()
                .map(request -> modelMapper.map(request, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestAssetDTO updateRequestAssetStatus(UpdateRequestAssetStatusDTO dto) {
        RequestAsset request = requestAssetRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found: " + dto.getRequestId()));

        String newStatus = dto.getStatus();
        String approverId = dto.getApproverId();

        if ("PENDING_AM".equals(newStatus)) {
            request.setStatus(RequestAssetStatus.PENDING_AM.name());
            request.setApprovedByDL(approverId);
            request.setApprovedByDLTime(Instant.now());
        } else if ("AM_APPROVED".equals(newStatus)) {
            request.setStatus(RequestAssetStatus.AM_APPROVED.name());
            request.setApprovedByAM(approverId);
            request.setApprovedByAMTime(Instant.now());
        } else if ("REJECTED".equals(newStatus)) {
            request.setStatus(RequestAssetStatus.REJECTED.name());
            request.setRejectionReason(dto.getRejectionReason());
        } else {
            request.setStatus(newStatus);
        }

        RequestAsset updated = requestAssetRepository.save(request);
        Instant now = Instant.now();
        String title = updated.getTitle();
        if (RequestAssetStatus.PENDING_AM.name().equals(updated.getStatus())) {
            List<User> managers = userRepository.findByRole_Id(4);
            String msg = "Asset request '" + title + "' is pending allocation";
            managers.forEach(m -> {
                notificationRepository.save(Notification.builder()
                        .notificationID(UUID.randomUUID().toString())
                        .user(m)
                        .message(msg)
                        .createDate(now)
                        .type(NotificationType.ALLOCATION_REQUEST)
                        .build());
            });
        } else if (RequestAssetStatus.AM_APPROVED.name().equals(updated.getStatus())) {
            String creatorId = updated.getCreateBy();
            if (creatorId != null) {
                userRepository.findById(creatorId).ifPresent(u -> {
                    notificationRepository.save(Notification.builder()
                            .notificationID(UUID.randomUUID().toString())
                            .user(u)
                            .message("Your asset request '" + title + "' has been approved")
                            .createDate(now)
                            .type(NotificationType.ALLOCATION_APPROVED)
                            .build());
                });
            }
        } else if (RequestAssetStatus.REJECTED.name().equals(updated.getStatus())) {
            String creatorId = updated.getCreateBy();
            if (creatorId != null) {
                String reason = updated.getRejectionReason();
                userRepository.findById(creatorId).ifPresent(u -> {
                    notificationRepository.save(Notification.builder()
                            .notificationID(UUID.randomUUID().toString())
                            .user(u)
                            .message("Your asset request '" + title + "' was rejected"
                                    + (reason != null ? ": " + reason : ""))
                            .createDate(now)
                            .type(NotificationType.ALLOCATION_REJECTED)
                            .build());
                });
            }
        }

        RequestAssetDTO result = modelMapper.map(updated, RequestAssetDTO.class);
        if (RequestAssetStatus.PENDING_AM.name().equals(updated.getStatus())) {
            userRepository.findById(approverId)
                    .ifPresent(dl -> result.setApprovedByDLName(dl.getFullName()));
        } else if (RequestAssetStatus.AM_APPROVED.name().equals(updated.getStatus())) {
            userRepository.findById(approverId)
                    .ifPresent(am -> result.setApprovedByAMName(am.getFullName()));
        }
        return result;
    }

    @Override
    public List<DepartmentLeaderRequestDTO> getDepartmentLeaderRequests(String departmentId) {
        List<RequestAsset> requests = requestAssetRepository.findRequestsForDepartmentLeader(departmentId);
        return requests.stream().map(request -> {
            DepartmentLeaderRequestDTO dto = new DepartmentLeaderRequestDTO();
            dto.setRequestId(request.getRequestId());
            dto.setDescription(request.getDescription());
            dto.setStartTime(request.getStartTime());
            dto.setEndTime(request.getEndTime());
            dto.setStatus(request.getStatus());
            dto.setQuantity(null);
            if (request.getAsset() != null) {
                dto.setAsset(modelMapper.map(request.getAsset(), AssetDTO.class));
            }
            if (request.getTask() != null) {
                dto.setTask(modelMapper.map(request.getTask(), TaskDTO.class));
            }
            if (request.getCreateBy() != null) {
                User user = userRepository.findById(request.getCreateBy()).orElse(null);
                if (user != null) {
                    dto.setRequesterInfo(modelMapper.map(user, UserDTO.class));
                }
            }

            if (request.getRequestAssetCategories() != null && !request.getRequestAssetCategories().isEmpty()) {
                List<RequestAssetCategoryDTO> categoryDTOs = request.getRequestAssetCategories()
                        .stream()
                        .map(cat -> modelMapper.map(cat, RequestAssetCategoryDTO.class))
                        .collect(Collectors.toList());
                dto.setCategories(categoryDTOs);
            }

            if (request.getTask() != null && request.getTask().getMilestone() != null
                    && request.getTask().getMilestone().getProject() != null) {
                dto.setProjectInfo(modelMapper.map(request.getTask().getMilestone().getProject(), ProjectDTO.class));
            }

            dto.setBookingType(request.getBookingType() != null ? request.getBookingType().name() : null);
            dto.setRecurrenceCount(request.getRecurrenceCount());
            dto.setRecurrenceInterval(request.getRecurrenceInterval());

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RequestAssetDTO> getRequestsByUser(String userId) {
        List<RequestAsset> requests = requestAssetRepository.findByUserId(userId);
        return requests.stream()
                .map(r -> modelMapper.map(r, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestAssetDTO> getRequestsForAssetManager() {
        List<String> allowedStatuses = Arrays.asList(
                "PENDING_AM",
                "AM_APPROVED",
                "PARTIALLY_ALLOCATED",
                "FULLY_ALLOCATED",
                "REJECTED",
                "CANCELLED",
                "PREPARED",
                "WAITING");
        List<RequestAsset> requests = requestAssetRepository.findByStatusIn(allowedStatuses);

        return requests.stream().map(request -> {
            RequestAssetDTO dto = modelMapper.map(request, RequestAssetDTO.class);

            if (request.getCreateBy() != null) {
                userRepository.findById(request.getCreateBy()).ifPresent(user -> {
                    dto.setRequesterInfo(modelMapper.map(user, UserDTO.class));
                });
            }
            if (request.getTask() != null && request.getTask().getMilestone() != null
                    && request.getTask().getMilestone().getProject() != null) {
                dto.setProjectInfo(modelMapper.map(request.getTask().getMilestone().getProject(), ProjectDTO.class));
            }

            if (request.getRequestAssetCategories() != null && !request.getRequestAssetCategories().isEmpty()) {
                List<RequestAssetCategoryDTO> categoryDTOs = request.getRequestAssetCategories()
                        .stream()
                        .map(cat -> {
                            RequestAssetCategoryDTO dtoCat = new RequestAssetCategoryDTO();
                            dtoCat.setCategoryID(cat.getCategory().getCategoryID());
                            dtoCat.setName(cat.getCategory().getName());
                            dtoCat.setQuantity(cat.getQuantity());
                            return dtoCat;
                        })
                        .collect(Collectors.toList());
                dto.setCategories(categoryDTOs);
            }

            if (request.getApprovedByDL() != null) {
                userRepository.findById(request.getApprovedByDL())
                        .ifPresent(dl -> dto.setApprovedByDLName(dl.getFullName()));
            }

            if (request.getApprovedByAM() != null) {
                userRepository.findById(request.getApprovedByAM())
                        .ifPresent(am -> dto.setApprovedByAMName(am.getFullName()));
            }

            return dto;
        }).collect(Collectors.toList());
    }
    @Override
    public List<RequestAssetDTO> createBookingRequests(CreateBookingRequestDTO dto) {
        List<Slot> slots = generateSlots(dto);

        List<RequestAssetDTO> results = new ArrayList<>();
        for (Slot slot : slots) {
            RequestAsset request = new RequestAsset();
            request.setRequestId(UUID.randomUUID().toString());
            request.setTitle(dto.getTitle());
            request.setDescription(dto.getDescription());
            request.setStartTime(slot.start);
            request.setEndTime(slot.end);
            request.setRequestTime(Instant.now());
            request.setStatus(RequestAssetStatus.BOOKED.name());
            request.setBookingType(dto.getBookingType());
            request.setRecurrenceType(dto.getRecurrenceType());
            request.setRecurrenceInterval(dto.getRecurrenceInterval());
            request.setRecurrenceEndDate(LocalDate.from(dto.getRecurrenceEndDate()));
            request.setSelectedDaysOfWeek(new HashSet<>(dto.getSelectedDays()));
            request.setDayOfMonth(dto.getDayOfMonth());
            request.setFallbackToLastDay(dto.getFallbackToLastDay());
            request.setRecurrenceCount(slots.size());
            if (dto.getTaskID() != null) {
                Task task = taskRepository.findById(dto.getTaskID())
                        .orElseThrow(() -> new RuntimeException("Task not found: " + dto.getTaskID()));
                request.setTask(task);
                if (task.getAssignee() != null) {
                    request.setCreateBy(task.getAssignee());
                }
            }
            Asset asset = assetRepository.findById(dto.getAssetID())
                    .orElseThrow(() -> new RuntimeException("Asset not found: " + dto.getAssetID()));
            if (!AssetStatus.AVAILABLE.name().equals(asset.getStatus())) {
                throw new IllegalStateException("Asset is not available for booking.");
            }
            request.setAsset(asset);
            RequestAsset saved = requestAssetRepository.save(request);
            results.add(modelMapper.map(saved, RequestAssetDTO.class));
        }
        return results;
    }

        private List<Slot> generateSlots(CreateBookingRequestDTO dto) {
            Instant baseStart = dto.getStartTime();
            Instant baseEnd   = dto.getEndTime();
            Duration duration = Duration.between(
                    LocalDateTime.ofInstant(baseStart, ZoneId.systemDefault()),
                    LocalDateTime.ofInstant(baseEnd,   ZoneId.systemDefault())
            );
            List<Slot> slots = new ArrayList<>();

            if (dto.getRecurrenceType() == RecurrenceType.NONE) {
                slots.add(new Slot(baseStart, baseEnd));
                return slots;
            }

            LocalDate startDate = baseStart.atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate   = dto.getRecurrenceEndDate();

            switch (dto.getRecurrenceType()) {
                case DAILY:
                    LocalDate d = startDate;
                    while (!d.isAfter(endDate)) {
                        Instant s = d.atTime(baseStart.atZone(ZoneId.systemDefault()).toLocalTime())
                                .atZone(ZoneId.systemDefault()).toInstant();
                        slots.add(new Slot(s, s.plus(duration)));
                        d = d.plusDays(dto.getRecurrenceInterval());
                    }
                    break;

                case WEEKLY:
                    Set<DayOfWeek> days = new HashSet<>(dto.getSelectedDays());
                    LocalDate w = startDate;
                    while (!w.isAfter(endDate)) {
                        if (days.contains(w.getDayOfWeek())) {
                            Instant s = w.atTime(baseStart.atZone(ZoneId.systemDefault()).toLocalTime())
                                    .atZone(ZoneId.systemDefault()).toInstant();
                            slots.add(new Slot(s, s.plus(duration)));
                        }
                        w = w.plusWeeks(dto.getRecurrenceInterval());
                    }
                    break;

                case MONTHLY:
                    LocalDate m = startDate;
                    while (!m.isAfter(endDate)) {
                        YearMonth ym = YearMonth.from(m);
                        int day = dto.getDayOfMonth();
                        int actual = Math.min(day, ym.lengthOfMonth());
                        LocalDate md = ym.atDay(actual);
                        Instant s = md.atTime(baseStart.atZone(ZoneId.systemDefault()).toLocalTime())
                                .atZone(ZoneId.systemDefault()).toInstant();
                        slots.add(new Slot(s, s.plus(duration)));
                        m = m.plusMonths(dto.getRecurrenceInterval());
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported recurrence type");
            }

            return slots;
        }

        private static class Slot {
            final Instant start, end;
            Slot(Instant start, Instant end) { this.start = start; this.end = end; }
        }


    @Override
    @Transactional
    public RequestAssetDTO createCategoryRequest(CreateCategoryRequestDTO dto) {
        RequestAsset requestAsset = new RequestAsset();
        requestAsset.setRequestId(UUID.randomUUID().toString());
        requestAsset.setTitle(dto.getTitle());
        requestAsset.setDescription(dto.getDescription());
        requestAsset.setStartTime(dto.getStartTime());
        requestAsset.setEndTime(dto.getEndTime());
        requestAsset.setRequestTime(Instant.now());
        requestAsset.setStatus(RequestAssetStatus.PENDING_LEADER.toString());
        requestAsset.setBookingType(BookingType.CATEGORY);
        Task task = null;
        if (dto.getTaskID() != null && !dto.getTaskID().isEmpty()) {
            task = taskRepository.findById(dto.getTaskID())
                    .orElseThrow(() -> new RuntimeException("Task not found: " + dto.getTaskID()));
            requestAsset.setTask(task);

            String assigneeId = task.getAssignee();
            if (assigneeId != null && !assigneeId.isEmpty()) {
                User assignee = userRepository.findById(assigneeId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + assigneeId));
                requestAsset.setCreateBy(assignee.getId());
            }
        }

        List<CreateCategoryRequestItemDTO> items = dto.getCategories();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one category must be provided.");
        }
        List<RequestAssetCategory> racs = new ArrayList<>();
        for (CreateCategoryRequestItemDTO item : items) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be > 0 for category " + item.getCategoryID());
            }
            Category cat = categoryRepository.findById(item.getCategoryID())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + item.getCategoryID()));
            RequestAssetCategory rac = new RequestAssetCategory();
            rac.setId(new RequestAssetCategoryId(requestAsset.getRequestId(), cat.getCategoryID()));
            rac.setRequestAsset(requestAsset);
            rac.setCategory(cat);
            rac.setQuantity(item.getQuantity());
            racs.add(rac);
        }
        requestAsset.setRequestAssetCategories(racs);
        RequestAsset saved = requestAssetRepository.save(requestAsset);
        if (task != null && task.getAssignee() != null) {
            userRepository.findById(task.getAssignee()).ifPresent(assignee -> {
                String deptId = assignee.getDepartment().getDepartmentId();
                List<User> leaders = userRepository.findByDepartment_DepartmentId(deptId).stream()
                        .filter(u -> u.getRole().getId() == 4)
                        .collect(Collectors.toList());

                Instant now = Instant.now();
                String msg = "New asset request '" + saved.getTitle() + "' awaiting your approval";

                leaders.forEach(leader -> {
                    Notification notif = Notification.builder()
                            .notificationID(UUID.randomUUID().toString())
                            .user(leader)
                            .message(msg)
                            .createDate(now)
                            .type(NotificationType.ALLOCATION_REQUEST)
                            .build();
                    notificationRepository.save(notif);
                });
            });
        }
        RequestAssetDTO result = modelMapper.map(saved, RequestAssetDTO.class);
        List<RequestAssetCategoryDTO> categoryDTOs = saved.getRequestAssetCategories().stream()
                .map(rac -> {
                    RequestAssetCategoryDTO catDto = new RequestAssetCategoryDTO();
                    catDto.setCategoryID(rac.getCategory().getCategoryID());
                    catDto.setName(rac.getCategory().getName());
                    catDto.setQuantity(rac.getQuantity());
                    return catDto;
                })
                .collect(Collectors.toList());
        result.setCategories(categoryDTOs);

        if (saved.getCreateBy() != null) {
            userRepository.findById(saved.getCreateBy())
                    .ifPresent(u -> result.setRequesterInfo(modelMapper.map(u, UserDTO.class)));
        }
        return result;
    }

    @Override
    @Transactional
    public RequestAssetDTO acceptCategoryRequest(String requestId) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        if (request.getRequestAssetCategories() == null
                || request.getRequestAssetCategories().isEmpty()) {
            throw new IllegalStateException("This is not a category-based request.");
        }
        request.setStatus(RequestAssetStatus.AM_APPROVED.name());
        RequestAsset updated = requestAssetRepository.save(request);
        String creatorId = updated.getCreateBy();
        if (creatorId != null) {
            userRepository.findById(creatorId).ifPresent(user -> {
                Notification notif = Notification.builder()
                        .notificationID(UUID.randomUUID().toString())
                        .user(user)
                        .message("Your asset request '" + updated.getTitle() + "' has been approved.")
                        .createDate(Instant.now())
                        .type(NotificationType.ALLOCATION_APPROVED)
                        .build();
                notificationRepository.save(notif);
            });
        }

        return modelMapper.map(updated, RequestAssetDTO.class);
    }

    @Override
    public RequestAssetDTO acceptBooking(String requestId, String approverId) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        if (request.getAsset() == null) {
            throw new IllegalStateException("This request is not asset-based and cannot be processed.");
        }
        String currentStatus = request.getStatus();
        if ("PENDING_LEADER".equals(currentStatus)) {
            request.setStatus(RequestAssetStatus.PENDING_AM.name());
            request.setApprovedByDL(approverId);
            request.setApprovedByDLTime(Instant.now());
            RequestAsset updatedRequest = requestAssetRepository.save(request);
            RequestAssetDTO dto = modelMapper.map(updatedRequest, RequestAssetDTO.class);
            User dlUser = userRepository.findById(approverId)
                    .orElseThrow(() -> new RuntimeException("Approver (DL) not found: " + approverId));
            dto.setApprovedByDLName(dlUser.getFullName());
            return dto;
        } else if ("PENDING_AM".equals(currentStatus)) {
            request.setStatus(RequestAssetStatus.AM_APPROVED.name());
            request.setApprovedByAM(approverId);
            request.setApprovedByAMTime(Instant.now());
            RequestAsset updatedRequest = requestAssetRepository.save(request);
            BorrowedAsset borrowed = new BorrowedAsset();
            borrowed.setBorrowedID(UUID.randomUUID().toString());
            borrowed.setAsset(request.getAsset());
            borrowed.setTask(request.getTask());
            borrowed.setBorrowTime(Instant.now());
            borrowed.setStartTime(request.getStartTime());
            borrowed.setEndTime(request.getEndTime());
            borrowed.setStatus(BorrowedAssetStatus.BOOKED.name());
            borrowed.setDescription("Accepted booking request " + requestId);
            borrowedAssetRepository.save(borrowed);
            AssetUsageHistory usage = new AssetUsageHistory();
            usage.setUsageID(UUID.randomUUID().toString());
            usage.setAsset(request.getAsset());
            if (request.getTask() != null && request.getTask().getMilestone() != null
                    && request.getTask().getMilestone().getProject() != null) {
                usage.setProject(request.getTask().getMilestone().getProject());
            } else {
                throw new RuntimeException("Cannot retrieve project information from the request's task.");
            }
            if (request.getCreateBy() != null) {
                User user = userRepository.findById(request.getCreateBy()).orElse(null);
                usage.setUser(user);
            }
            usage.setStartDate(request.getStartTime());
            usage.setEndDate(request.getEndTime());
            usage.setStatus("In Use");
            assetUsageHistoryRepository.save(usage);
            RequestAssetDTO dto = modelMapper.map(updatedRequest, RequestAssetDTO.class);
            User amUser = userRepository.findById(approverId)
                    .orElseThrow(() -> new RuntimeException("Approver (AM) not found: " + approverId));
            dto.setApprovedByAMName(amUser.getFullName());
            return dto;
        } else if (RequestAssetStatus.AM_APPROVED.name().equals(currentStatus)) {
            throw new RuntimeException("Request already fully approved.");
        } else {
            throw new RuntimeException("Request is in an invalid state for approval: " + currentStatus);
        }
    }
    public List<RequestAssetDTO> getRequestByTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
        List<RequestAsset> requests = requestAssetRepository.findByTask(task);
        return requests.stream()
                .map(requestAsset -> modelMapper.map(requestAsset, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }
    /**
     * Kiểm tra tình trạng khả dụng của tài sản trong một yêu cầu mượn tài sản.
     * Phân loại yêu cầu mượn thành:
     * - Theo tài sản cụ thể
     * - Theo danh mục (category-based)
     *
     * @param requestId ID của yêu cầu mượn
     * @return Kết quả kiểm tra khả dụng, bao gồm danh sách tài sản còn sẵn hoặc danh mục bị thiếu
     */
    @Override
    public CheckAvailabilityResult checkAssetAvailabilityAndReturnAssets(String requestId) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        CheckAvailabilityResult result = new CheckAvailabilityResult();

        if (request.getAsset() != null) {
            String assetId = request.getAsset().getAssetID();
            boolean isBorrowed = borrowedAssetRepository.existsActiveBorrow(assetId);
            boolean isReturned = returnedAssetRepository.existsReturnedAssetByAssetID(assetId);

            if (!isBorrowed || isReturned) {
                Asset asset = assetRepository.findById(assetId)
                        .orElseThrow(() -> new RuntimeException("Asset not found: " + assetId));
                result.addAvailableAsset(modelMapper.map(asset, AssetDTO.class));
                result.setAvailable(true);
                result.setMessage("Asset is available.");
            } else {
                result.setAvailable(false);
                result.setMessage("Asset is currently borrowed and not yet returned.");
            }
            return result;
        }
        boolean allCategoriesSatisfied = true;

        for (RequestAssetCategory rac : request.getRequestAssetCategories()) {
            String categoryId = rac.getCategory().getCategoryID();
            int quantityRequested = rac.getQuantity();

            List<Asset> availableAssets = assetRepository.findAvailableAssetsByCategory(categoryId).stream()
                    .filter(asset -> !borrowedAssetRepository.existsAssetConflict(asset.getAssetID(), request.getStartTime(), request.getEndTime()))
                    .toList();

            if (availableAssets.size() < quantityRequested) {
                allCategoriesSatisfied = false;
                int shortage = quantityRequested - availableAssets.size();

                Instant nextAvailableTime = estimateNextAvailableTimeForCategory(categoryId, shortage, request.getEndTime());
                result.addMissingCategory(
                        rac.getCategory().getCategoryID(),
                        rac.getCategory().getName(),
                        quantityRequested,
                        availableAssets.size(),
                        nextAvailableTime
                );

            } else {
                List<AssetDTO> assetDTOs = availableAssets.subList(0, quantityRequested)
                        .stream()
                        .map(asset -> modelMapper.map(asset, AssetDTO.class))
                        .toList();
                result.getAvailableAssets().addAll(assetDTOs);
            }
        }

        result.setAvailable(allCategoriesSatisfied);
        result.setMessage(allCategoriesSatisfied ? "All categories have sufficient assets." : "Some categories are missing required assets.");
        return result;
    }
    /**
     * Tìm thời gian sớm nhất có thể mượn đủ số lượng asset trong category
     */
    private Instant estimateNextAvailableTimeForCategory(String categoryId, int requiredQuantity, Instant afterTime) {
        List<Asset> allAssets = assetRepository.findAvailableAssetsByCategory(categoryId);

        return allAssets.stream()
                .map(asset -> borrowedAssetRepository.findNextAvailableTime(asset.getAssetID(), afterTime))
                .filter(Objects::nonNull)
                .sorted()
                .limit(requiredQuantity)
                .reduce((first, second) -> second)  // lấy thời gian lớn nhất trong nhóm
                .orElse(null);
    }

    @Override
    public List<AssetDTO> getAllocatedAssetsByRequestId(String requestId) {
        List<RequestAssetAllocation> allocations = requestAssetAllocationRepository.findByRequestAsset_RequestId(requestId);

        return allocations.stream()
                .map(allocation -> modelMapper.map(allocation.getAsset(), AssetDTO.class))
                .collect(Collectors.toList());
    }


}
