package com.sc_fleetfinder.fleets.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class ConfirmationRequiredException extends RuntimeException {
    private final Object dto;

    public ConfirmationRequiredException(String directive, Object dto) {
        super(directive);
        this.dto = dto;
    }
}
