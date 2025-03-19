package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "Attachment")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Attachment implements Serializable {
    @Id
    @Column(name = "AttachmentID", length = 50, nullable = false)
    private String attachmentId;
    @Column(name = "FileName", length = 200)
    private String fileName;

    @Column(name = "FileURL", columnDefinition = "nvarchar(max)")
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "TaskID", referencedColumnName = "TaskID")
    private Task task;
    @ManyToOne
    @JoinColumn(name = "UploadByID", referencedColumnName = "ID")
    private User uploadedBy;
}
