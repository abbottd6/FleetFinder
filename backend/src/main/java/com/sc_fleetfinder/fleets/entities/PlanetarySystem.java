package com.sc_fleetfinder.fleets.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name="planetary_system")
@Getter
@Setter
public class PlanetarySystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="system_id")
    private int systemId;

    @Column(name="system_name")
    private String systemName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="planetarySystem")
    private Set<PlanetMoonSystem> planetMoonSystems;
}
