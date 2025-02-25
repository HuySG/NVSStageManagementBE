package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
public class Department {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "ID", nullable = false, length = 50)
    private String id;

    @Size(max = 250)
    @Nationalized
    @Column(name = "Name", length = 250)
    private String name;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

}