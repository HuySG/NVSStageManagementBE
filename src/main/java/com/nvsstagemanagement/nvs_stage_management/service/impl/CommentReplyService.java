package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.commentReply.CommentReplyDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Comment;
import com.nvsstagemanagement.nvs_stage_management.model.CommentReply;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.repository.CommentReplyRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.CommentRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import com.nvsstagemanagement.nvs_stage_management.service.ICommentReplyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentReplyService implements ICommentReplyService {
    private final CommentReplyRepository commentReplyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public CommentReplyDTO createReply(CommentReplyDTO replyDTO) {
        if (replyDTO.getCommentID() == null || replyDTO.getCommentID().isEmpty()) {
            throw new IllegalArgumentException("Comment ID is required");
        }
        if (replyDTO.getUserID() == null || replyDTO.getUserID().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (replyDTO.getReplyText() == null || replyDTO.getReplyText().isEmpty()) {
            throw new IllegalArgumentException("Reply text is required");
        }

        Comment comment = commentRepository.findById(replyDTO.getCommentID())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + replyDTO.getCommentID()));

        User user = userRepository.findById(replyDTO.getUserID())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + replyDTO.getUserID()));

        CommentReply commentReply = new CommentReply();
        commentReply.setReplyID(UUID.randomUUID().toString());
        commentReply.setComment(comment);
        commentReply.setUser(user);
        commentReply.setReplyText(replyDTO.getReplyText());
        commentReply.setStatus("Active");
        commentReply.setCreatedDate(LocalDateTime.now());
        commentReply.setLastModifiedDate(LocalDateTime.now());

        CommentReply savedReply = commentReplyRepository.save(commentReply);
        return modelMapper.map(savedReply, CommentReplyDTO.class);
    }

    @Override
    public CommentReplyDTO getReplyById(String replyID) {
        CommentReply reply = commentReplyRepository.findById(replyID)
                .orElseThrow(() -> new IllegalArgumentException("Reply not found: " + replyID));
        return modelMapper.map(reply, CommentReplyDTO.class);
    }

    @Override
    public CommentReplyDTO updateReply( CommentReplyDTO replyDTO) {
        CommentReply existingReply = commentReplyRepository.findById(replyDTO.getReplyID())
                .orElseThrow(() -> new IllegalArgumentException("Reply not found: " + replyDTO.getReplyID()));

        if (replyDTO.getReplyText() != null && !replyDTO.getReplyText().isEmpty()) {
            existingReply.setReplyText(replyDTO.getReplyText());
        }
        if (replyDTO.getStatus() != null && !replyDTO.getStatus().isEmpty()) {
            existingReply.setStatus(replyDTO.getStatus());
        }
        existingReply.setCreatedDate(existingReply.getCreatedDate());
        existingReply.setLastModifiedDate(LocalDateTime.now());

        CommentReply updatedReply = commentReplyRepository.save(existingReply);
        return modelMapper.map(updatedReply, CommentReplyDTO.class);
    }

    @Override
    public boolean deleteReply(String replyID) {
        if (!commentReplyRepository.existsById(replyID)) {
            throw new IllegalArgumentException("Reply not found: " + replyID);
        }
        commentReplyRepository.deleteById(replyID);
        return true;
    }

    @Override
    public List<CommentReplyDTO> getAllRepliesByComment(String commentID) {
        List<CommentReply> replies = commentReplyRepository.findByComment_CommentID(commentID);
        return replies.stream()
                .map(reply -> modelMapper.map(reply, CommentReplyDTO.class))
                .collect(Collectors.toList());
    }
}
