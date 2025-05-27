package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.notification.NotificationDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.NotificationType;
import com.nvsstagemanagement.nvs_stage_management.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @PostMapping("/create")
    public void createNotification(@RequestParam String userId,
                                   @RequestParam String message,
                                   @RequestParam NotificationType type) {
        notificationService.createNotification(userId, message, type);
    }

    @GetMapping("/user/{userId}")
    public List<NotificationDTO> getNotifications(@PathVariable String userId) {
        return notificationService.getNotificationsByUser(userId);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
