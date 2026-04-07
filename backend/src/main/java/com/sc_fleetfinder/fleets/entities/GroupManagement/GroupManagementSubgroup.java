package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="group_management_subgroup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupManagementSubgroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_subgroup")
    private Long subgroupId;

    @ManyToOne
    @JoinColumn(name="listing_id", referencedColumnName="id_group", nullable = false)
    @NotNull(message="GroupManagementSubgroup entity field groupListing cannot be null.")
    private GroupListing groupListing;

    @ManyToOne
    @JoinColumn(name="parent_subgroup_id", referencedColumnName="id_subgroup", nullable = true)
    private GroupManagementSubgroup parentSubgroup;

    @Column(name="subgroup_label", nullable = false, columnDefinition="VARCHAR(64) NULL")
    @NotNull(message="GroupManagementSubgroup entity field subgroupLabel cannot be null.")
    private String subgroupLabel;

    @Column(name="subgroup_notes", nullable = true)
    private String subgroupNotes;

    @Column(name="intended_subgroup_size", nullable = true)
    private Integer intendedSubgroupSize;

    @Column(name="sort_order", nullable = false, columnDefinition="TINYINT NOT NULL DEFAULT 1")
    private Integer sortOrder = 1;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;
}
