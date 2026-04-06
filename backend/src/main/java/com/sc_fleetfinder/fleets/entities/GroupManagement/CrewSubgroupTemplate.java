package com.sc_fleetfinder.fleets.entities.GroupManagement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private CrewTemplate template;

    @ManyToOne
    @JoinColumn(name="parent_subgroup_id", referencedColumnName="id_template_subgroup")
    private CrewSubgroupTemplate parentSubgroup;

    @Column(name = "subgroup_label", columnDefinition = "VARCHAR(64) NOT NULL")
    private String subgroupLabel;

    @Column(name = "subgroup_notes")
    private String subgroupNotes;

    @Column(name = "intended_subgroup_size", columnDefinition = "TINYINT")
    private Integer intendedSubgroupSize;
}
