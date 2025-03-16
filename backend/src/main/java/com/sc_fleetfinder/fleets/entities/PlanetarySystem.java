package com.sc_fleetfinder.fleets.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="planetary_system")
@Getter
@Setter
public class PlanetarySystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="system_id")
    private Integer systemId;

    @Column(name="system_name")
    private String systemName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="planetarySystem", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<PlanetMoonSystem> planetMoonSystems = new HashSet<>();
}
