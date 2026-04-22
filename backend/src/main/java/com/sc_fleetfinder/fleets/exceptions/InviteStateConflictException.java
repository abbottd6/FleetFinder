package com.sc_fleetfinder.fleets.exceptions;

import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupInvitationStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Getter
public class InviteStateConflictException extends RuntimeException {

    private final GroupInvitationStatus persistenceStatus;
    private final GroupInvitationStatus auxiliaryStatus;
    private final Long inviteId;

    public InviteStateConflictException(GroupInvitationStatus persistenceStatus,
                                        GroupInvitationStatus auxiliaryStatus,
                                        Long inviteId) {
        
        
        super(String.format("Action failed. The status of this invite was already changed" +
                " to: %s", persistenceStatus.toString()));
        
        this.persistenceStatus = persistenceStatus;
        this.auxiliaryStatus = auxiliaryStatus;
        this.inviteId = inviteId;
    }
}
