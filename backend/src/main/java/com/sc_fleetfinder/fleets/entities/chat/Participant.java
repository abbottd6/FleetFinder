package com.sc_fleetfinder.fleets.entities.chat;

import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.ConversationParticipantId;
import com.sc_fleetfinder.fleets.utils.ConversationParticipantRole;
import com.sc_fleetfinder.fleets.utils.ConversationType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="conversation_participant")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Participant {

    public Participant(Users user, Conversation conv, ConversationParticipantRole role) {
        this.participantId = new ConversationParticipantId(
                user.getUserId(), conv.getConversationId());

        this.conversation = conv;
        this.user = user;
        this.role = role;
        this.lastActiveAt = Instant.now();
    }

    @EmbeddedId
    private ConversationParticipantId participantId;

    @MapsId("conversationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_conversation", nullable = false)
    private Conversation conversation;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="id_user", nullable = false)
    private Users user;

    @Column(name="role", nullable = false)
    private ConversationParticipantRole role;

    @CreationTimestamp
    @Column(name="joined_at", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant joinedAt;

    @Column(name="left_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant leftAt;

    @OneToOne
    @JoinColumn(name="last_read_message_id")
    private Message lastReadMessage;

    @Column(name="last_active_at")
    private Instant lastActiveAt;

    @Column(name="is_archived", nullable = false)
    private boolean isArchived = false;

    @Column(name="muting", nullable = false)
    private boolean isMuting = false;

    @OneToOne
    @JoinColumn(name="deleted_up_to_message_id")
    private Message deletedUpToMessage;
}
