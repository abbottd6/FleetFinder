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
@Table(name="gameplay_subcategory")
@Getter
@Setter
public class GameplaySubcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="subcategory_id")
    private int subcategoryId;

    @Column(name="subcategory_name")
    private String subcategoryName;

    @ManyToOne
    @JoinColumn(name="category_id", nullable = false)
    private GameplayCategory gameplayCategory;
}
