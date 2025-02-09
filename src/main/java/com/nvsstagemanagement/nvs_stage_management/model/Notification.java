package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
public class Notification {
    @Id
    @Nationalized
    @Column(name = "NotificationID", nullable = false, length = 50)
    private String notificationID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User userID;

    @Nationalized
    @Column(name = "Message", length = 50)
    private String message;

    @Column(name = "CreaateDate")
    private Instant creaateDate;

    @Nationalized
    @Column(name = "Type", length = 10)
    private String type;

}