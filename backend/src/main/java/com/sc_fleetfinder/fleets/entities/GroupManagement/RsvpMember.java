package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RsvpStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="rsvp_member")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsvpMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_rsvp_member")
    private Long rsvpId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="rsvp_master_id", referencedColumnName="id_rsvp_master", nullable = false)
    private RsvpMaster rsvpMaster;

    @ManyToOne
    @JoinColumn(name="listing_id", referencedColumnName="id_group", nullable = false)
    private GroupListing groupListing;

    @OneToOne
    @JoinColumn(name="position_id", referencedColumnName="id_position", nullable = true)
    private CrewPosition position;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName="id_user", nullable = false)
    private Users user;

    @Column(name="rsvp_message")
    private String rsvpMessage;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private RsvpStatus status;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name="batch_id", columnDefinition = "BINARY(16) NOT NULL", nullable = false)
    private UUID batchId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="expires_at", nullable = false)
    private Instant expiresAt;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;
}
