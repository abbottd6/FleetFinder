package com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat;

import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ConfirmUnmuteConvDto {
    private String otherUsername;
    private Long conversationId;

    public ConfirmUnmuteConvDto(Users mutedParticipant,
                         Conversation conversation) {
        this.otherUsername = mutedParticipant.getUsername();
        this.conversationId = conversation.getConversationId();
    }
}
