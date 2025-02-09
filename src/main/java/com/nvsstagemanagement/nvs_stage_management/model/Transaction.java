package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Transactions")
public class Transaction {
    @Id
    @Nationalized
    @Column(name = "TransactionID", nullable = false, length = 50)
    private String transactionID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TicketID")
    private Ticket ticketID;

    @Nationalized
    @Column(name = "PaymentMethod", length = 50)
    private String paymentMethod;

    @ColumnDefault("getdate()")
    @Column(name = "TransactionDate")
    private Instant transactionDate;

    @Column(name = "TotalAmount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

}