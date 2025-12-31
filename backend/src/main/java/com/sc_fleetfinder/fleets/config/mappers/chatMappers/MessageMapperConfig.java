package com.sc_fleetfinder.fleets.config.mappers.chatMappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.utils.MessageType;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sc_fleetfinder.fleets.entities.chat.Message;

@Configuration
public class MessageMapperConfig {

    public MessageMapperConfig() {}

    @Bean
    public ModelMapper messageMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Message.class, GetMessageDto.class)
                .addMappings(mapper -> {

                    mapper.map(Message::getMsgId, GetMessageDto::setMsgId);

                    mapper.using(ctx -> {
                        Conversation conv = (Conversation) ctx.getSource();
                        return conv != null ? conv.getConversationId() : null;
                    }).map(Message::getConversation, GetMessageDto::setConversationId);

                    mapper.using(ctx -> {
                        Participant sender = (Participant) ctx.getSource();
                        return sender != null ? sender.getUser().getUserId() : null;
                    }).map(Message::getSender, GetMessageDto::setSenderId);

                    mapper.using(ctx -> {
                        Participant sender = (Participant) ctx.getSource();
                        return sender != null ? sender.getUser().getUsername() : null;
                    }).map(Message::getSender, GetMessageDto::setSenderName);

                    mapper.using(ctx -> {
                        MessageType type = (MessageType) ctx.getSource();
                        return type != null ? type.toString() : null;
                    }).map(Message::getMessageType, GetMessageDto::setMessageType);

                    mapper.map(Message::getMsgBody, GetMessageDto::setMsgBody);

                    mapper.map(Message::getCreatedAt, GetMessageDto::setCreatedAt);

                    mapper.map(Message::getUpdatedAt, GetMessageDto::setUpdatedAt);

                    mapper.map(Message::getDeletedAt, GetMessageDto::setDeletedAt);

                    mapper.using(ctx -> {
                        Message repliedTo = (Message) ctx.getSource();
                        return repliedTo != null ? repliedTo.getMsgId() : null;
                    }).map(Message::getRepliedToMessage, GetMessageDto::setRepliedToMessageId);

                    mapper.map(Message::getClientMessageId, GetMessageDto::setClientMessageId);
                });
        return modelMapper;
    }
}
