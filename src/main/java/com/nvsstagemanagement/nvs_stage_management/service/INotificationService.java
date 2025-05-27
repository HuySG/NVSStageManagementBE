package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.notification.NotificationDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.NotificationType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface INotificationService {
    void createNotification(String userId, String message, NotificationType type);
    List<NotificationDTO> getNotificationsByUser(String userId);
    void deleteNotification(String notificationId);
}
