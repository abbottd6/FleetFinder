package com.sc_fleetfinder.fleets.utils;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ParentEntityReference {

    @Column(name = "parent_entity_id")
    private Long parentEntityId;

    @Column(name= "parent_entity_type")
    private String parentEntityType;
}
