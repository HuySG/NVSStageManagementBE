package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.commentReply.CommentReplyDTO;
import com.nvsstagemanagement.nvs_stage_management.service.ICommentReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment-reply")
@RequiredArgsConstructor
public class CommentReplyController {
    private final ICommentReplyService commentReplyService;
    @PostMapping
    public ResponseEntity<CommentReplyDTO> createReply(@RequestBody CommentReplyDTO replyDTO) {
        CommentReplyDTO createdReply = commentReplyService.createReply(replyDTO);
        return ResponseEntity.status(201).body(createdReply);
    }

    @GetMapping("/comment-reply")
    public ResponseEntity<CommentReplyDTO> getReplyById(@RequestParam String replyID) {
        CommentReplyDTO reply = commentReplyService.getReplyById(replyID);
        return ResponseEntity.ok(reply);
    }

    @PutMapping
    public ResponseEntity<CommentReplyDTO> updateReply(@RequestBody CommentReplyDTO replyDTO) {
        CommentReplyDTO updatedReply = commentReplyService.updateReply(replyDTO);
        return ResponseEntity.ok(updatedReply);
    }

    @DeleteMapping("/{replyID}")
    public ResponseEntity<Void> deleteReply(@RequestParam String replyID) {
        commentReplyService.deleteReply(replyID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comment")
    public ResponseEntity<List<CommentReplyDTO>> getAllRepliesByComment(@RequestParam String commentID) {
        List<CommentReplyDTO> replies = commentReplyService.getAllRepliesByComment(commentID);
        return ResponseEntity.ok(replies);
    }
}
