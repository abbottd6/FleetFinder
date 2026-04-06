package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupInvitationStatus;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupRosterClass;
import com.sc_fleetfinder.fleets.utils.GroupManagement.InviteDirection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="group_invite")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_invite")
    private Long inviteId;

    @ManyToOne
    @JoinColumn(name="listing_id", referencedColumnName="id_group", nullable=false)
    private GroupListing groupListing;

    @ManyToOne
    @JoinColumn(name="sender_id", referencedColumnName="id_user", nullable = false)
    private Users sender;

    @ManyToOne
    @JoinColumn(name="recipient_id", referencedColumnName="id_user", nullable = false)
    private Users recipient;

    @Enumerated(EnumType.STRING)
    @Column(name="direction", nullable = false)
    private InviteDirection inviteDirection;

    @Enumerated(EnumType.STRING)
    @Column(name="roster_class", nullable = false)
    private GroupRosterClass rosterClass;

    @Enumerated(EnumType.STRING)
    @Column(name="invite_status", nullable = false)
    private GroupInvitationStatus inviteStatus;

    @Column(name="invite_message", nullable = true)
    private String inviteMessage;

    @Column(name="expires_at", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant expiresAt;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;
}
