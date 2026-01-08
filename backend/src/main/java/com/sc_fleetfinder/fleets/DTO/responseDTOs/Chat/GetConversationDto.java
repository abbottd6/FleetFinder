package com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat;

import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class GetConversationDto {
    private Long conversationId;
    private String conversationType;
    private String conversationTitle;
    private Long otherUserId;
    private String otherUserName;
    private Instant createdAt;
    private Instant updatedAt;
    private Long lastMsgId;
    private Long currentUserId;
    private String currentUserName;
    private String dmKey;

    public GetConversationDto(Conversation conv,
                              Users currentUser,
                              Users otherUser) {
        this.conversationId = conv.getConversationId();
        this.conversationType = conv.getConvType() != null ? conv.getConvType().toString() : null;
        this.conversationTitle = conv.getTitle();
        this.currentUserId = currentUser.getUserId();
        this.currentUserName = currentUser.getUsername();
        this.otherUserId = otherUser.getUserId();
        this.otherUserName = otherUser.getUsername();
        this.createdAt = conv.getCreatedAt();
        this.updatedAt = conv.getUpdatedAt();
        this.lastMsgId = conv.getLastMsg() != null ? conv.getLastMsg().getMsgId() : null;
        this.dmKey = conv.getDmKey();
    }
}
