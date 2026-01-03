package com.sc_fleetfinder.fleets.exceptions.handlers;

import com.sc_fleetfinder.fleets.exceptions.ConfirmationRequiredException;
import com.sc_fleetfinder.fleets.exceptions.ConversationIntegrityException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
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
    public ResponseEntity<Map<String, String>> handleIntegrityException(ConversationIntegrityException e) {
        log.error("Conversation integrity error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("code", "CONVERSATION_INTEGRITY",
                        "message", "Internal error retrieving conversation participants."));
    }

    @ExceptionHandler(ConfirmationRequiredException.class)
    public ResponseEntity<Map<String, String>> handleConfirmationRequiredException(ConfirmationRequiredException e) {
        log.error("Confirmation required error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("code", "CONFIRMATION_REQUIRED",
                        "message", e.getMessage()));
    }

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException e) {
//        log.error("Resource not found error: {}", e.getMessage());
//
//        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
//        pd.setTitle("NOT_FOUND");
//        pd.setDetail(e.getMessage());
//
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
//    }
}