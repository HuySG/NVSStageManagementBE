package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
public class Notification {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "NotificationID", nullable = false, length = 50)
    private String notificationID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User userID;

    @Size(max = 50)
    @Nationalized
    @Column(name = "Message", length = 50)
    private String message;

    @Column(name = "CreaateDate")
    private OffsetDateTime creaateDate;

    @Size(max = 10)
    @Nationalized
    @Column(name = "Type", length = 10)
    private String type;

}