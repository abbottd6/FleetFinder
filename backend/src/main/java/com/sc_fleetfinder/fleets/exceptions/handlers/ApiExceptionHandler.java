package com.sc_fleetfinder.fleets.exceptions.handlers;

import com.sc_fleetfinder.fleets.exceptions.ConfirmationRequiredException;
import com.sc_fleetfinder.fleets.exceptions.ConversationIntegrityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(ConversationIntegrityException.class)
    public ResponseEntity<Map<String, String>> handleIntegrityException(ConversationIntegrityException e) {
        log.error("Conversation integrity error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Internal error retrieving conversation participants."));
    }

    @ExceptionHandler(ConfirmationRequiredException.class)
    public ResponseEntity<Map<String, String>> handleConfirmationRequiredException(ConfirmationRequiredException e) {
        log.error("Confirmation required error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("code", "CONFIRMATION_REQUIRED",
                        "message", e.getMessage()));
    }
}
