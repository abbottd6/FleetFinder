package com.sc_fleetfinder.fleets.exceptions;

public class SkipExternalNotificationProcessingException extends RuntimeException {
    public SkipExternalNotificationProcessingException(String reasonToSkip) {
        super(reasonToSkip);
    }
}
