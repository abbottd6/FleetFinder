package com.sc_fleetfinder.fleets.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="planet_moon_system")
@Getter
@Setter
public class PlanetMoonSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="planet_id")
    private Integer planetId;

    @Column(name="planet_name")
    private String planetName;

    @ManyToOne
    @JoinColumn(name="system_id", nullable = false)
    @JsonBackReference
    private PlanetarySystem planetarySystem;
}
