package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.notification.NotificationDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.NotificationType;
import com.nvsstagemanagement.nvs_stage_management.model.Notification;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.repository.NotificationRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import com.nvsstagemanagement.nvs_stage_management.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Tạo notification mới.
     * @param userId người nhận
     * @param message nội dung
     * @param type loại thông báo
     */
    @Override
    public void createNotification(String userId, String message, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setNotificationID(UUID.randomUUID().toString());
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreateDate(Instant.now());
        notification.setType(type);

        notificationRepository.save(notification);
    }

    /**
     * Lấy danh sách thông báo theo người dùng với phân trang.
     * @param userId ID người dùng
     * @param page số trang (bắt đầu từ 0)
     * @param size số lượng phần tử mỗi trang
     * @return Danh sách NotificationDTO theo trang
     */
    @Override
    public Page<NotificationDTO> getNotificationsByUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByUser_Id(userId, pageable)
                .map(notification -> modelMapper.map(notification, NotificationDTO.class));
    }

    @Override
    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
