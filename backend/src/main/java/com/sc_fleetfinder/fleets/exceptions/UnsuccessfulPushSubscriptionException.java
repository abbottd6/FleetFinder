package com.sc_fleetfinder.fleets.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
public class UnsuccessfulPushSubscriptionException extends RuntimeException{
    public UnsuccessfulPushSubscriptionException(String message) { super (message); }
}
