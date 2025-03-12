package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
public class ShowType {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "showTypeID", nullable = false, length = 50)
    private String showTypeID;

    @NotNull
    @Nationalized
    @Lob
    @Column(name = "TypeName", nullable = false)
    private String typeName;

}