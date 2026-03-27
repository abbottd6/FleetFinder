package com.sc_fleetfinder.fleets.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ContentLimitException extends RuntimeException {
    public ContentLimitException(Long userId, String entityType, Integer limit) {
        super(String.format("%s are limited to a quantity of [%d]. User [%d] has reached this limit.",
                entityType, limit, userId));
    }
}
