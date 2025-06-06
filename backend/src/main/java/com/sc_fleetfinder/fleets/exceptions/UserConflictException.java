package com.sc_fleetfinder.fleets.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Thrown during user creation if a given email, username, or keycloakId already exists.
 * Returns HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserConflictException extends RuntimeException {
    public UserConflictException(String message) {
        super(message);
    }
}
