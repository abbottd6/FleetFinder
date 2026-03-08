package com.sc_fleetfinder.fleets.entities.chat;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.SendMessageDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.MessageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

@Entity
@Table(name="message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    public Message(Conversation conv, Users sender, Message repliedTo,
                   SendMessageDto dto) {

        this.conversation = conv;
        this.sender = sender;
        this.messageType = MessageType.fromString(dto.getMessageType());
        this.msgBody = dto.getMsgBody();
        this.repliedToMessage = repliedTo;
        this.clientMessageId = dto.getClientMessageId();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_msg")
    private Long msgId;

    @ManyToOne
    @JoinColumn(name="id_conversation", nullable = false)
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name="id_sender", nullable = false)
    private Users sender;

    @Enumerated(EnumType.STRING)
    @Column(name="message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT;

    @Column(name="msg_body", nullable = false, columnDefinition="TEXT")
    private String msgBody;

    @CreationTimestamp
    @Column(name="created_at", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name="edited_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant updatedAt;

    @Column(name="deleted_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant deletedAt;

    @Column(name="received_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant receivedAt;

    @OneToOne
    @JoinColumn(name="replied_to_message_id")
    private Message repliedToMessage;

    @Column(name="client_message_id")
    private String clientMessageId;

    @Column(name="metadata")
    private String metadata = null;
}
