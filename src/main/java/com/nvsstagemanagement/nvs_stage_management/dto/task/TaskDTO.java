package com.nvsstagemanagement.nvs_stage_management.dto.task;

import com.nvsstagemanagement.nvs_stage_management.dto.attachment.AttachmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TaskDTO {
    private String taskID;
    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    @Size(max = 50, message = "Priority must not exceed 50 characters")
    private String priority;
    @Size(max = 50, message = "Tag must not exceed 50 characters")
    private String tag;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String assigneeID;
    private UserDTO assigneeInfo;
    private String createBy;
    private LocalDate createDate;
    private String updateBy;
    private LocalDate updateDate;
    private List<AttachmentDTO> attachments;
    private List<WatcherDTO> watchers;

    @NotBlank(message = "Milestone ID is required")
    private String milestoneId;

}
