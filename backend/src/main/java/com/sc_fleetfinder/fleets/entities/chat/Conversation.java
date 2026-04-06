package com.sc_fleetfinder.fleets.entities.chat;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.ConversationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="conversation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {

    public Conversation(Users user,
                 FindOrStartNewConversationDto dto,
                 String dmKey) {
        this.convType = dto.getConvType();
        this.title = dto.getTitle();
        this.initUser = user;
        this.lastMsg = null;
        this.dmKey = dmKey;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_conversation")
    private Long conversationId;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable=false)
    private ConversationType convType;

    @Column(name="title", length = 255)
    private String title;

    @ManyToOne
    @JoinColumn(name="initiated_by_user_id", nullable = false)
    private Users initUser;

    @CreationTimestamp
    @Column(name="created_at", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name="updated_at", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant updatedAt;

    @OneToOne
    @JoinColumn(name="last_msg_id", nullable = true)
    private Message lastMsg;

    // hash of minUserId:maxUserId. null for group chats.
    @Column(name="dm_key", length = 64, unique = true, nullable = true)
    private String dmKey;

    @OneToOne
    @JoinColumn(name="listing_id", nullable = true)
    private GroupListing groupListing;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy="conversation", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Participant> participants = new HashSet<>();
}
