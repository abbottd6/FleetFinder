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
@Table(name="game_experience")
@Data
@Cacheable
public class GameExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="experience_id")
    private int experienceId;

    @Column(name="experience_type")
    private String experienceType;
}
