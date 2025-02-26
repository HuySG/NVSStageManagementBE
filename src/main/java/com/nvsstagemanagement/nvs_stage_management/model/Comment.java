package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "Comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @Column(length = 50, nullable = false)
    private String commentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskID", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(nullable = false)
    private String commentText;

    @Column(name = "CreateDate", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "LastModifiedDate")
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Column(nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
    private String status;
}
