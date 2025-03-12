package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.attachment.AttachmentDTO;

public interface IAttachmentService {
    AttachmentDTO addAttachmentToTask( AttachmentDTO attachmentDTO);
    void deleteAttachment(String attachmentId);
}
