package com.nvsstagemanagement.nvs_stage_management.service;


import com.nvsstagemanagement.nvs_stage_management.dto.comment.CommentDTO;

import java.util.List;

public interface ICommentService {
    CommentDTO createComment(CommentDTO commentDTO);
    CommentDTO updateComment(CommentDTO commentDTO);
    boolean hardDeleteComment(String commentID);
    List<CommentDTO> getCommentsByTask(String taskID);
}
