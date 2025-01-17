package com.sc_fleetfinder.fleets.entities;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="legality")
@Data
public class Legality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="legality_id")
    private int legalityId;

    @Column(name="legality")
    private String legalityStatus;
}