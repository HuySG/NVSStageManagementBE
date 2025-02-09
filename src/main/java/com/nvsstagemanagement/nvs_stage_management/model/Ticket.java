package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Tickets")
public class Ticket {
    @Id
    @Nationalized
    @Column(name = "TicketID", nullable = false, length = 50)
    private String ticketID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EventID")
    private Event eventID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerID")
    private User customerID;

    @ColumnDefault("getdate()")
    @Column(name = "PurchaseDate")
    private Instant purchaseDate;

    @Nationalized
    @ColumnDefault("'Not Checked-In'")
    @Column(name = "CheckInStatus", length = 50)
    private String checkInStatus;

}