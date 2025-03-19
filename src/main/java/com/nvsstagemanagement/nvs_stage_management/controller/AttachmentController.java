package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.attachment.AttachmentDTO;
import com.nvsstagemanagement.nvs_stage_management.service.impl.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<AttachmentDTO> addAttachmentToTask(@RequestBody AttachmentDTO attachmentDTO) {
        AttachmentDTO savedAttachment = attachmentService.addAttachmentToTask( attachmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAttachment);
    }

    @DeleteMapping("/attachmentId")
    public ResponseEntity<Void> deleteAttachment(@RequestParam String attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
