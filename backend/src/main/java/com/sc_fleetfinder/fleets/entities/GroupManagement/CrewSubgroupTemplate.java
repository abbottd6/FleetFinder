package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionSubgroupDto;
import com.sc_fleetfinder.fleets.utils.GroupManagement.SubgroupDropListOrientation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.util.LinkedHashSet;

@Entity
@Table(name = "crew_subgroup_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrewSubgroupTemplate {

    public CrewSubgroupTemplate(Long templateId, Long parentTemplateSubgroupId, GroupCompositionSubgroupDto subgroupDto) {
        this.templateId = templateId;
        this.parentSubgroupId = parentTemplateSubgroupId;
        this.subgroupLabel = subgroupDto.getSubgroupLabel();
        this.subgroupNotes = subgroupDto.getSubgroupNotes();
        this.sortOrder = subgroupDto.getSortOrder();
        this.dropListOrientation = subgroupDto.getDropListOrientation();
    }

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

    @Column(name="sort_order", nullable = false, columnDefinition="TINYINT NOT NULL DEFAULT 0")
    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(name="drop_list_orientation", nullable = false)
    private SubgroupDropListOrientation dropListOrientation;
}
