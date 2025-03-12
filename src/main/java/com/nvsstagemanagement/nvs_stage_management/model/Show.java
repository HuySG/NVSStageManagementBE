package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
public class Show {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "ShowID", nullable = false, length = 50)
    private String showID;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "Title", nullable = false)
    private String title;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @Nationalized
    @Lob
    @Column(name = "Content")
    private String content;

    @Column(name = "StartTime")
    private Instant startTime;

    @Column(name = "EndTime")
    private Instant endTime;

    @Size(max = 50)
    @Nationalized
    @Column(name = "CreatedBy", length = 50)
    private String createdBy;
    @OneToMany(mappedBy = "show", fetch = FetchType.LAZY)
    private List<Task> tasks;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ShowTypeID", nullable = false)
    private ShowType showType;
}