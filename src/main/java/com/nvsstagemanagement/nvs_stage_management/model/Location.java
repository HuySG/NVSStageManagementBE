package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
public class Location {
    @Id
    @Nationalized
    @Column(name = "LocationID", nullable = false, length = 50)
    private String locationID;

    @Nationalized
    @Column(name = "LocationName", length = 50)
    private String locationName;

    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;

}