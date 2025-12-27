package com.sc_fleetfinder.fleets.utils;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationParticipantId implements Serializable {

    private Long conversationId;
    private Long userId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(!(obj instanceof ConversationParticipantId that)) return false;
        return Objects.equals(conversationId, that.conversationId)
                && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, userId);
    }
}
