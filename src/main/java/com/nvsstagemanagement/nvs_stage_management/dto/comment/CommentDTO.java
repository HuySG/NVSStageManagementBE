package com.nvsstagemanagement.nvs_stage_management.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private String commentID;
    private String taskID;
    private String userID;
    private String commentText;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String status;
    private String parentCommentID;

}
