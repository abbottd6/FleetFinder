package com.sc_fleetfinder.fleets.entities.GroupManagement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;

@Entity
@Table(name = "crew_subgroup_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrewSubgroupTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template_subgroup")
    private Long templateSubgroupId;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name="parent_subgroup_id")
    private Long parentSubgroupId;

    @Column(name = "subgroup_label", columnDefinition = "VARCHAR(64) NOT NULL")
    private String subgroupLabel;

    @Column(name = "subgroup_notes")
    private String subgroupNotes;

    @Column(name="sort_order", nullable = false, columnDefinition="TINYINT NOT NULL DEFAULT 1")
    private Integer sortOrder = 1;

    @Column(name = "intended_subgroup_size", columnDefinition = "TINYINT")
    private Integer intendedSubgroupSize;
}
