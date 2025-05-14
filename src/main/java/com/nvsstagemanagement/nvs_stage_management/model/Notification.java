package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "NotificationID", nullable = false, length = 50)
    private String notificationID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User user;

    @Size(max = 255)
    @Nationalized
    @Column(name = "Message", length = 255)
    private String message;

    @Column(name = "CreateDate")
    private Instant createDate;

    @Column(name = "Type", length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationType type;


}