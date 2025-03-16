package com.sc_fleetfinder.fleets.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="play_style")
@Data
public class PlayStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="style_id")
    private Integer styleId;

    @Column(name="play_style")
    private String playStyle;
}
