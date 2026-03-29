package com.sc_fleetfinder.fleets.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class UnsuccessfulPushSubscriptionException extends RuntimeException{
    public UnsuccessfulPushSubscriptionException(String message) { super (message); }
}
