package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberId;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberStatus;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RsvpStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="group_member")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class GroupMember {

    //OWNER CONSTRUCTOR
    public GroupMember(GroupListing listing, Users user, GroupMemberStatus memberStatus,
                       InGroupRank rank, Boolean extNotes) {
        this.groupMemberId = new GroupMemberId(listing.getGroupId(), user.getUserId());
        this.groupListing = listing;
        this.user = user;
        this.memberStatus = memberStatus;
        this.memberRank = rank;
        this.hasExtNotes = extNotes;
    }

    //JOINING MEMBER CONSTRUCTOR
    public GroupMember(GroupListing listing, Users newMember, Boolean hasMic, GroupMemberStatus memberStatus,
                       Boolean hasHeadset, InGroupRank rank, Boolean hasExtNotes) {
        this.groupMemberId = new GroupMemberId(listing.getGroupId(), newMember.getUserId());
        this.groupListing = listing;
        this.user = newMember;
        this.memberStatus = memberStatus;
        this.memberRank = rank;
        this.hasMic = hasMic;
        this.hasHeadset = hasHeadset;
        this.hasExtNotes = hasExtNotes;
    }

    @EmbeddedId
    private GroupMemberId groupMemberId;

    @MapsId("listingId")
    @ManyToOne
    @JoinColumn(name="listing_id", referencedColumnName="id_group")
    private GroupListing groupListing;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName="id_user")
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name="member_status")
    @NotNull(message="GroupMember entity field 'memberStatus' cannot be null.")
    private GroupMemberStatus memberStatus = GroupMemberStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name="in_group_rank_id", referencedColumnName="id_rank")
    private InGroupRank memberRank;

    @Column(name="has_mic", nullable = false)
    private Boolean hasMic = false;

    @Column(name="has_headset", nullable = false)
    private Boolean hasHeadset = false;

    @Column(name="member_note")
    private String memberNote;

    @Column(name="has_ext_notes")
    @NotNull(message="GroupMember entity field 'hasExtNotes' cannot be null.")
    private Boolean hasExtNotes = false;

    @Column(name="rsvp_status")
    @Enumerated(EnumType.STRING)
    private RsvpStatus rsvpStatus;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;
}
