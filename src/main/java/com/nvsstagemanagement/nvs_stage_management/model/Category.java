package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Category {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "CategoryID", nullable = false, length = 50)
    private String categoryID;

    @Size(max = 50)
    @Nationalized
    @Column(name = "Name", length = 50)
    private String name;

    @OneToMany(mappedBy = "categoryID")
    private Set<Asset> assets = new LinkedHashSet<>();

}