package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="rsvp_master")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsvpMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_rsvp_master")
    private Long idRsvpMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="listing_id", referencedColumnName="id_group", nullable = false)
    private GroupListing groupListing;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="subgroup_id", referencedColumnName="id_subgroup", nullable = true)
    private GroupManagementSubgroup subgroup;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="scheduled_ts", nullable = false)
    private Instant scheduledTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name="rsvp_message", nullable = true)
    private String rsvpMessage;

    @Column(name="comms_share", nullable = true)
    private String commsShare;

    @Column(name="sent_flag", nullable = false)
    private Boolean sentFlag;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;
}
