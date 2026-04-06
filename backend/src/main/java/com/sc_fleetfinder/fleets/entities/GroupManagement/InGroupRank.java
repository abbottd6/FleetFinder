package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
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
@Table(name = "in_group_rank")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InGroupRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_rank")
    private Long rankId;

    @ManyToOne
    @JoinColumn(name="listing_id", referencedColumnName="id_group")
    private GroupListing groupListing;

    @ManyToOne
    @JoinColumn(name="rank_scope_id", referencedColumnName="id_subgroup")
    private GroupManagementSubgroup rankSubgroupScope;

    @Column(name="rank_title", nullable = false, columnDefinition = "VARCHAR(32) NOT NULL")
    private String rankTitle;

    @Column(name="rank_notes", nullable = true, columnDefinition = "VARCHAR(64) NULL")
    private String rankNotes;

    @ManyToOne
    @JoinColumn(name="created_by_id", referencedColumnName="id_user", nullable = true)
    private Users createdByUser;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;
}
