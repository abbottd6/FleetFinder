package com.sc_fleetfinder.fleets.config.mappers.chatMappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.utils.ConversationType;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConversationMapperConfig {

    public ConversationMapperConfig() {}

    @Bean
    public ModelMapper conversationMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Conversation.class, GetConversationDto.class)
                .addMappings(mapper -> {

                    mapper.map(Conversation::getConversationId, GetConversationDto::setConversationId);

                    mapper.using(ctx -> {
                        ConversationType type = (ConversationType) ctx.getSource();
                        return type != null ? type.toString() : null;
                    }).map(Conversation::getConvType, GetConversationDto::setConversationType);

                    mapper.map(Conversation::getTitle, GetConversationDto::setConversationTitle);

                    mapper.using(ctx -> {
                        Users initiator = (Users) ctx.getSource();
                        return initiator != null ? initiator.getUsername() : null;
                    }).map(Conversation::getInitUser, GetConversationDto::setInitiatingUserName);

                    mapper.using(ctx -> {
                        Users initiator = (Users) ctx.getSource();
                        return initiator != null ? initiator.getUserId() : null;
                    }).map(Conversation::getInitUser, GetConversationDto::setInitiatingUserId);

                    mapper.map(Conversation::getCreatedAt, GetConversationDto::setCreatedAt);

                    mapper.map(Conversation::getUpdatedAt, GetConversationDto::setUpdatedAt);

                    mapper.using(ctx -> {
                        Message last = (Message) ctx.getSource();
                        return last != null ? last.getMsgId() : null;
                    }).map(Conversation::getLastMsg, GetConversationDto::setLastMsgId);

                    mapper.using(ctx -> {
                        Message last = (Message) ctx.getSource();
                        return last != null ? last.getSender().getUser().getUserId() : null;
                    }).map(Conversation::getLastMsg, GetConversationDto::setLastSenderUserId);

                    mapper.using(ctx -> {
                        Message last = (Message) ctx.getSource();
                        return last != null ? last.getSender().getUser().getUsername() : null;
                    }).map(Conversation::getLastMsg, GetConversationDto::setLastSenderUserName);

                    mapper.using(ctx -> {
                        Message last = (Message) ctx.getSource();
                        return last != null ? last.getMsgBody() : null;
                    }).map(Conversation::getLastMsg, GetConversationDto::setLastMsgBody);

                    mapper.map(Conversation::getDmKey, GetConversationDto::setDmKey);
                });
        return modelMapper;
    }
}
