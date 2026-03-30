package com.sc_fleetfinder.fleets.utils;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParentEntityReference {

    @Column(name = "parent_entity_id")
    private Long parentEntityId;

    @Column(name= "parent_entity_type")
    private String parentEntityType;
}
