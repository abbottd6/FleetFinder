package com.sc_fleetfinder.fleets.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class ActionNotAuthorizedException extends RuntimeException {
    public ActionNotAuthorizedException(Long requestingUserId, String requestedAction,
                                 String requestedEntityType, Long entityId) {
        super(String.format("User [%d] is not authorized to perform %s action on %s with ID: [%d]",
                requestingUserId, requestedAction, requestedEntityType, entityId));
    }
}
