package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class EventType {
    @Id
    @Size(max = 50)
    @Nationalized
    @Column(name = "EventTypeID", nullable = false, length = 50)
    private String eventTypeID;

    @NotNull
    @Nationalized
    @Lob
    @Column(name = "TypeName", nullable = false)
    private String typeName;

    @OneToMany(mappedBy = "eventType")
    private Set<Event> events = new LinkedHashSet<>();

    @OneToMany(mappedBy = "eventTypeID")
    private Set<EventAssetPermission> eventAssetPermissions = new LinkedHashSet<>();

}