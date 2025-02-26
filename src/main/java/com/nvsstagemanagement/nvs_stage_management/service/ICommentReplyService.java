package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.commentReply.CommentReplyDTO;

import java.util.List;

public interface ICommentReplyService {
    CommentReplyDTO createReply(CommentReplyDTO replyDTO);
    CommentReplyDTO getReplyById(String replyID);
    CommentReplyDTO updateReply( CommentReplyDTO replyDTO);
    boolean deleteReply(String replyID);
    List<CommentReplyDTO> getAllRepliesByComment(String commentID);
}
