package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.comment.CommentDTO;
import com.nvsstagemanagement.nvs_stage_management.service.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentDTO commentDTO) {
            CommentDTO createdComment = commentService.createComment(commentDTO);
            return ResponseEntity.ok(createdComment);
    }
    @DeleteMapping("/commentID")
    public ResponseEntity<Void> deleteComment(@RequestParam String commentID) {
        try {
            commentService.hardDeleteComment(commentID);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PutMapping
    public ResponseEntity<CommentDTO> updateComment( @RequestBody CommentDTO commentDTO) {
        try {
            CommentDTO updatedComment = commentService.updateComment(commentDTO);
            return ResponseEntity.ok(updatedComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    @GetMapping("/task")
    public ResponseEntity<List<CommentDTO>> getCommentsByTask(@RequestParam String taskID) {
        List<CommentDTO> commentDTOs = commentService.getCommentsByTask(taskID);
        return ResponseEntity.ok(commentDTOs);
    }
}
