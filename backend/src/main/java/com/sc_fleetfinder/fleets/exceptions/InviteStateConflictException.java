package com.sc_fleetfinder.fleets.exceptions;

import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupInviteStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Getter
public class InviteStateConflictException extends RuntimeException {

    private final GroupInviteStatus persistenceStatus;
    private final GroupInviteStatus auxiliaryStatus;
    private final Long inviteId;

    public InviteStateConflictException(GroupInviteStatus persistenceStatus,
                                        GroupInviteStatus auxiliaryStatus,
                                        Long inviteId) {
        
        
        super(String.format("Action failed. The status of this invite was already changed" +
                " to: %s", persistenceStatus.toString()));
        
        this.persistenceStatus = persistenceStatus;
        this.auxiliaryStatus = auxiliaryStatus;
        this.inviteId = inviteId;
    }
}
