package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
public class Location {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "LocationID", nullable = false, length = 50)
    private String locationID;

    @Size(max = 50)
    @Nationalized
    @Column(name = "LocationName", length = 50)
    private String locationName;

    @Size(max = 50)
    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;

}