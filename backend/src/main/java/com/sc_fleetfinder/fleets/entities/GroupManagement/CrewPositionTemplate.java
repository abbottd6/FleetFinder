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
@Table(name = "crew_position_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrewPositionTemplate {

    public CrewPositionTemplate(Long templateRootId, Long subgroupTemplateId,
                                CrewRoleClassification positionRole, String positionNotes,
                                Integer sortOrder) {
        this.templateRootId = templateRootId;
        this.subgroupTemplateId = subgroupTemplateId;
        this.positionRole = positionRole;
        this.positionNotes = positionNotes;
        this.sortOrder = sortOrder;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template_position")
    private Long templatePositionId;

    @Column(name = "root_template_id", nullable = false)
    private Long templateRootId;

    @Column(name = "subgroup_template_id", nullable = false)
    private Long subgroupTemplateId;

    @ManyToOne
    @JoinColumn(name = "position_role_id")
    private CrewRoleClassification positionRole;

    @Column(name = "position_notes")
    private String positionNotes;

    @Column(name="sort_order", nullable = false, columnDefinition="TINYINT NOT NULL DEFAULT 0")
    private Integer sortOrder = 0;
}
