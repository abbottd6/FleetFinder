package com.sc_fleetfinder.fleets.unit_tests.exceptions;

import com.sc_fleetfinder.fleets.exceptions.ConfirmationRequiredException;
import com.sc_fleetfinder.fleets.exceptions.ContentLimitException;
import com.sc_fleetfinder.fleets.exceptions.ConversationIntegrityException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.exceptions.handlers.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ApiExceptionHandlerTest {

    private ApiExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ApiExceptionHandler();
    }

    @Test
    void handleConvIntegrity_Returns500WithConversationIntegrityCode() {
        ConversationIntegrityException ex = new ConversationIntegrityException("integrity error");

        ResponseEntity<Map<String, String>> response = handler.handleConvIntegrity(ex);

        assertAll("handleConvIntegrity assertions:",
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
                () -> assertThat(response.getBody()).containsEntry("code", "CONVERSATION_INTEGRITY"),
                () -> assertThat(response.getBody()).containsKey("message")
        );
    }

    @Test
    void handleConfirmationRequired_Returns409WithCodeMessageAndDto() {
        Object dto = Map.of("listingId", 42L);
        ConfirmationRequiredException ex = new ConfirmationRequiredException("Please confirm deletion", dto);

        ResponseEntity<Map<String, Object>> response = handler.handleConfirmationRequired(ex);

        assertAll("handleConfirmationRequired assertions:",
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT),
                () -> assertThat(response.getBody()).containsEntry("code", "CONFIRMATION_REQUIRED"),
                () -> assertThat(response.getBody()).containsEntry("message", "Please confirm deletion"),
                () -> assertThat(response.getBody()).containsEntry("dto", dto)
        );
    }

    @Test
    void handleNotFound_Returns404WithNotFoundErrorAndMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("GroupListing", 99L);

        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertAll("handleNotFound assertions:",
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody()).containsEntry("error", "NOT_FOUND"),
                () -> assertThat(response.getBody()).containsKey("message")
        );
    }

    @Test
    void handleContentExcess_Returns403WithForbiddenError() {
        ContentLimitException ex = new ContentLimitException(7L, "Templates", 5);

        ResponseEntity<Map<String, Object>> response = handler.handleContentExcess(ex);

        assertAll("handleContentExcess assertions:",
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(response.getBody()).containsEntry("error", "FORBIDDEN"),
                () -> assertThat(response.getBody()).containsKey("message")
        );
    }
}
