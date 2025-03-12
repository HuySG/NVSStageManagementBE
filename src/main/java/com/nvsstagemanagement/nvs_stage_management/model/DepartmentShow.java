package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DepartmentShow {
    @EmbeddedId
    private DepartmentShowId id;

    @MapsId("showId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ShowId", nullable = false)
    private Show show;

    @MapsId("departmentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DepartmentId", nullable = false)
    private Department department;

}