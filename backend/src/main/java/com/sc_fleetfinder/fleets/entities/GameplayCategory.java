package com.sc_fleetfinder.fleets.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="gameplay_category")
@Getter
@Setter
@Cacheable
public class GameplayCategory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="category_id")
    private int categoryId;

    @Column(name="category_name")
    private String categoryName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="gameplayCategory", fetch = FetchType.EAGER)
    private Set<GameplaySubcategory> gameplaySubcategories = new HashSet<>();
}
