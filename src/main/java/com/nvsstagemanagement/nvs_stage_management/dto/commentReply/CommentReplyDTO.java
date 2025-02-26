package com.nvsstagemanagement.nvs_stage_management.dto.commentReply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReplyDTO {
    private String replyID;
    private String commentID;
    private String userID;
    private String replyText;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String status;
}