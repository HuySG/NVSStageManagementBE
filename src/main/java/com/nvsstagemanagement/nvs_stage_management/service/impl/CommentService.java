package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.comment.CommentDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Comment;
import com.nvsstagemanagement.nvs_stage_management.model.Task;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.repository.CommentRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import com.nvsstagemanagement.nvs_stage_management.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {
        if (commentDTO.getTaskID() == null || commentDTO.getTaskID().isEmpty()) {
            throw new IllegalArgumentException("Task ID is required");
        }
        if (commentDTO.getUserID() == null || commentDTO.getUserID().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (commentDTO.getCommentText() == null || commentDTO.getCommentText().isEmpty()) {
            throw new IllegalArgumentException("Comment text is required");
        }

        Comment comment = new Comment();
        Task task = taskRepository.findById(commentDTO.getTaskID())
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + commentDTO.getTaskID()));
        comment.setTask(task);

        User user = userRepository.findById(commentDTO.getUserID())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + commentDTO.getUserID()));
        comment.setUser(user);

        comment.setCommentText(commentDTO.getCommentText());
        comment.setCreatedDate(LocalDateTime.now());
        comment.setStatus("Active");

        if (commentDTO.getParentCommentID() != null && !commentDTO.getParentCommentID().isEmpty()) {
            Comment parentComment = commentRepository.findById(commentDTO.getParentCommentID())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found: " + commentDTO.getParentCommentID()));
            comment.setParentComment(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        return modelMapper.map(savedComment, CommentDTO.class);
    }

    @Override
    public CommentDTO updateComment(CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentDTO.getCommentID())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentDTO.getCommentID()));

        if (commentDTO.getCommentText() != null && !commentDTO.getCommentText().isEmpty()) {
            comment.setCommentText(commentDTO.getCommentText());
        }
        if (commentDTO.getStatus() != null && !commentDTO.getStatus().isEmpty()) {
            comment.setStatus(commentDTO.getStatus());
        }
        comment.setLastModifiedDate(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(comment);
        return modelMapper.map(updatedComment, CommentDTO.class);
    }

    @Override
    public boolean hardDeleteComment(String commentID) {
        if (commentRepository.existsById(commentID)) {
            commentRepository.deleteById(commentID);
            return true;
        }
        throw new IllegalArgumentException("Comment not found with ID: " + commentID);
    }

    @Override
    public List<CommentDTO> getCommentsByTask(String taskID) {
        List<Comment> comments = commentRepository.findByTask_TaskID(taskID);
        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentDTO.class))
                .collect(Collectors.toList());
    }
}
