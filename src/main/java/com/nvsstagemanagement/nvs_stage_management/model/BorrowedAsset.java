package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
 import lombok.Setter;
 import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "BorrowedAsset")
@Getter
@Setter
@NoArgsConstructor
 @AllArgsConstructor
 public class BorrowedAsset {

     @Id
     @Column(name = "BorrowedID", length = 50, nullable = false)
     private String borrowedID;
     @ManyToOne
     @JoinColumn(name = "AssetID", referencedColumnName = "AssetID", nullable = false)
     private Asset asset;

     @ManyToOne
     @JoinColumn(name = "TaskID", referencedColumnName = "TaskID", nullable = false)
     private Task task;

    @Column(name = "BorrowTime", nullable = false)
    private Instant borrowTime;
    @Column(name = "EndTime", nullable = false)
    private Instant endTime;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;
  }
