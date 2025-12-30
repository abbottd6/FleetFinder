package com.sc_fleetfinder.fleets.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConfirmationRequiredException extends RuntimeException {
    public ConfirmationRequiredException(String message) {
        super(message);
    };
}
