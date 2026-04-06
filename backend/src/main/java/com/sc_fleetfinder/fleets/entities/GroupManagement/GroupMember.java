package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberId;
import com.sc_fleetfinder.fleets.utils.GroupManagement.MemberStatusOptions;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class GroupMember {

    //OWNER CONSTRUCTOR
    public GroupMember(GroupListing listing, Users user, MemberStatusOptions status,
                       InGroupRank rank, Boolean extNotes) {
        this.groupListing = listing;
        this.user = user;
        this.memberStatus = status;
        this.memberRank = rank;
        this.hasExtNotes = extNotes;
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
    private MemberStatusOptions memberStatus;

    @ManyToOne
    @JoinColumn(name="in_group_rank_id", referencedColumnName="id_rank")
    private InGroupRank memberRank;

    @Column(name="has_comms")
    private Boolean hasComms;

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
