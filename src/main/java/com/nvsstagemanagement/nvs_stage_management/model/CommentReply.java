package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "CommentReply")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CommentReply {
    @Id
    @Column(name = "ReplyID", length = 50, nullable = false)
    private String replyID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CommentID", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(name = "ReplyText", nullable = false)
    private String replyText;

    @CreatedDate
    @Column(name = "CreatedDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "LastModifiedDate")
    private LocalDateTime lastModifiedDate;

    @Column(name = "Status", nullable = false, length = 50)
    private String status;
}
