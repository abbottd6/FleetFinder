package com.sc_fleetfinder.fleets.entities;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="game_environment")
@Data
@Cacheable
public class GameEnvironment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="environment_id")
    private int environmentId;

    @Column(name="environment_type")
    private String environmentType;
}
