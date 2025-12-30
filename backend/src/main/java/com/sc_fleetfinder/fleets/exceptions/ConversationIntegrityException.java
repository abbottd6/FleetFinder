package com.sc_fleetfinder.fleets.exceptions;

public class ConversationIntegrityException extends RuntimeException {
    public ConversationIntegrityException(String message) {
        super(message);
    }

    public ConversationIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}
