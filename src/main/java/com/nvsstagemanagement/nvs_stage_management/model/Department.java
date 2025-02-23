package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "Department")
public class Department {
    @Id
    @Nationalized
    @Column(name = "ID", nullable = false, length = 50)
    private String id;

    @Nationalized
    @Column(name = "Name", length = 250)
    private String name;

    @Nationalized
    @Lob
    @Column(name = "Discription")
    private String discription;

}