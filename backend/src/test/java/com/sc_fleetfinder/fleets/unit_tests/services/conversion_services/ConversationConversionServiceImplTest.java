package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Conversation;
import com.sc_fleetfinder.fleets.entities.chat.Participant;
import com.sc_fleetfinder.fleets.exceptions.ConversationIntegrityException;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.ConversationConversionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class ConversationConversionServiceImplTest {

    // No mocks needed — ConversationConversionServiceImpl is stateless.
    // @InjectMocks handles instantiation via reflection despite the package-private constructor.
    @InjectMocks
    private ConversationConversionServiceImpl service;

    private Users currentUser;
    private Users otherUser;
    private Conversation conv;
    private Participant currentPart;
    private Participant otherPart;

    @BeforeEach
    void setUp() {
        currentUser = new Users();
        currentUser.setUserId(1L);
        currentUser.setUsername("currentUser");
        currentUser.setKeycloakId("current-kc-id");

        otherUser = new Users();
        otherUser.setUserId(2L);
        otherUser.setUsername("otherUser");
        otherUser.setKeycloakId("other-kc-id");

        conv = new Conversation();
        conv.setConversationId(10L);
        conv.setTitle("Test Chat");

        currentPart = new Participant();
        currentPart.setUser(currentUser);

        otherPart = new Participant();
        otherPart.setUser(otherUser);
    }

    @Test
    void convertToDto_WithBothParticipants_ReturnsCorrectDto() {
        conv.setParticipants(Set.of(currentPart, otherPart));

        GetConversationDto result = service.convertToDto(conv, 1L);

        assertAll("convertToDto with both participants present:",
                () -> assertThat(result.getCurrentUserId()).isEqualTo(1L),
                () -> assertThat(result.getOtherUserId()).isEqualTo(2L),
                () -> assertThat(result.getConversationId()).isEqualTo(10L)
        );
    }

    @Test
    void convertToDto_WhenOtherParticipantMissing_ThrowsConversationIntegrityException() {
        // Only the current user's participant is in the set
        conv.setParticipants(Set.of(currentPart));

        assertThatThrownBy(() -> service.convertToDto(conv, 1L))
                .isInstanceOf(ConversationIntegrityException.class)
                .hasMessageContaining("missing the other participant");
    }

    @Test
    void convertToDto_WhenCurrentParticipantMissing_ThrowsConversationIntegrityException() {
        // Only the other user's participant is in the set
        conv.setParticipants(Set.of(otherPart));

        assertThatThrownBy(() -> service.convertToDto(conv, 1L))
                .isInstanceOf(ConversationIntegrityException.class)
                .hasMessageContaining("missing the current participant");
    }

    @Test
    @Disabled("convertToEntity is an unimplemented stub — method body only logs an error and returns null")
    void convertToEntity_IsUnimplementedAndReturnsNull() {
        GetConversationDto dto = new GetConversationDto();
        Conversation result = service.convertToEntity(dto);
        assertThat(result).isNull();
    }
}
