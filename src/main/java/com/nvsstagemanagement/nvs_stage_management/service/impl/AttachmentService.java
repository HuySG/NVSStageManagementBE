package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.attachment.AttachmentDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Attachment;
import com.nvsstagemanagement.nvs_stage_management.model.Task;
import com.nvsstagemanagement.nvs_stage_management.repository.AttachmentRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IAttachmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService implements IAttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    @Override
    public AttachmentDTO addAttachmentToTask( AttachmentDTO attachmentDTO) {
        Task task = taskRepository.findById(attachmentDTO.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found: " + attachmentDTO.getTaskId()));
        Attachment attachment = modelMapper.map(attachmentDTO, Attachment.class);
        if (attachment.getAttachmentId() == null || attachment.getAttachmentId().trim().isEmpty()) {
            attachment.setAttachmentId(UUID.randomUUID().toString());
        }
        attachment.setTask(task);
        Attachment savedAttachment = attachmentRepository.save(attachment);
        return modelMapper.map(savedAttachment, AttachmentDTO.class);
    }

    @Override
    public void deleteAttachment(String attachmentId) {
        if (!attachmentRepository.existsById(attachmentId)) {
            throw new RuntimeException("Attachment not found: " + attachmentId);
        }
        attachmentRepository.deleteById(attachmentId);
    }
}
