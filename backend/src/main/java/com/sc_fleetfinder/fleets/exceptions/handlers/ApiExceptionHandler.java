package com.sc_fleetfinder.fleets.exceptions.handlers;

import com.sc_fleetfinder.fleets.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(ConversationIntegrityException.class)
    public ResponseEntity<Map<String, String>> handleConvIntegrity(ConversationIntegrityException e) {
        log.error("Conversation integrity error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("code", "CONVERSATION_INTEGRITY",
                        "message", "Internal error retrieving conversation participants."));
    }

    @ExceptionHandler(ConfirmationRequiredException.class)
    public ResponseEntity<Map<String, Object>> handleConfirmationRequired(ConfirmationRequiredException e) {
        log.error("Confirmation required error: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("code", "CONFIRMATION_REQUIRED",
                        "message", e.getMessage(),
                        "dto", e.getDto()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found error: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "NOT_FOUND",
                        "message", e.getMessage())
        );
    }

    @ExceptionHandler(ContentLimitException.class)
    public ResponseEntity<Map<String, Object>> handleContentExcess(ContentLimitException e) {
        log.info("User has reached a content limit: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of("error", "FORBIDDEN",
                        "message", e.getMessage())
        );
    }

    @ExceptionHandler(InviteStateConflictException.class)
    public ResponseEntity<Map<String, Object>> handleInviteStateConflict(InviteStateConflictException e) {
        log.warn("Invite state conflict occurred for invite: {}. \n" +
                "Persisted state: {} \n Auxiliary state: {}",
                e.getInviteId(), e.getPersistenceStatus(), e.getAuxiliaryStatus());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("error", "CONFLICT",
                        "message", e.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        log.warn(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                e.getMessage()
        );
    }
}