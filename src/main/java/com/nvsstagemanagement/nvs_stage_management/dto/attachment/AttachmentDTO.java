package com.nvsstagemanagement.nvs_stage_management.dto.attachment;

import lombok.Data;

@Data
public class AttachmentDTO {
    private String attachmentId;
    private String fileName;
    private String fileUrl;
    private String taskId;
    private String uploadedById;
}
