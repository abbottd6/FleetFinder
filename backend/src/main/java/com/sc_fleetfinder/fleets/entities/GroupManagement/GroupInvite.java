package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupInviteStatus;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
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

    //Invite Offer

    //Invite request
    public GroupInvite(Users sender, Users recipient, GroupListing listing, InviteDirection dir,
                       GroupMemberStatus memberStatus, CrewRoleClassification role,
                       GroupInviteStatus status, String message, Instant expiresAt) {
        this.groupListing = listing;
        this.sender = sender;
        this.recipient = recipient;
        this.inviteDirection = dir;
        this.memberStatus = memberStatus;
        this.inviteRole = role;
        this.inviteStatus = status;
        this.inviteMessage = message;
        this.expiresAt = expiresAt;
        this.active = true;
        this.senderDismissed = false;
        this.recipientDismissed = false;
    }

    //blocking invite
    public GroupInvite(GroupInvite invite, GroupMemberStatus groupMemberStatus) {
        this.groupListing = invite.getGroupListing();
        this.sender = invite.getSender();
        this.recipient = invite.getRecipient();
        this.inviteDirection = InviteDirection.REQUEST;
        this.memberStatus = groupMemberStatus;
        this.inviteStatus = GroupInviteStatus.PENDING;
        this.active = true;
        this.recipientDismissed = true;
        this.senderDismissed = true;
    }

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
    private GroupMemberStatus memberStatus;

    @ManyToOne
    @JoinColumn(name="role_id", referencedColumnName="id_role", nullable = true)
    private CrewRoleClassification inviteRole;

    @Enumerated(EnumType.STRING)
    @Column(name="invite_status", nullable = false)
    private GroupInviteStatus inviteStatus;

    @Column(name="invite_message", nullable = true)
    private String inviteMessage;

    @Column(name="has_mic", nullable = false)
    private Boolean hasMic = false;

    @Column(name="has_headset", nullable = false)
    private Boolean hasHeadset = false;

    //deduplication column. set to 1 for pending invites, set to null on all other actions
    //enables unique constraint on un-actioned invites, while allowing duplicates when active is null
    @Column(name="active", nullable = true)
    private Boolean active;

    @Column(name="sender_dismissed", nullable = false)
    private Boolean senderDismissed;

    @Column(name="recipient_dismissed", nullable = false)
    private Boolean recipientDismissed;

    @Column(name="expires_at", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant expiresAt;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;
}
