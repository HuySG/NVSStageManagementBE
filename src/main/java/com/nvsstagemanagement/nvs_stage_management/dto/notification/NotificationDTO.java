package com.nvsstagemanagement.nvs_stage_management.dto.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nvsstagemanagement.nvs_stage_management.enums.NotificationType;
import lombok.Data;

import java.time.Instant;

@Data
public class NotificationDTO {
    private String notificationID;
    private String userId;
    private String message;
    private Instant createDate;
    private NotificationType type;
    @JsonProperty("isRead")
    private boolean isRead;
}
