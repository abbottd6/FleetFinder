package com.sc_fleetfinder.fleets.events;

import com.sc_fleetfinder.fleets.entities.Users;

public class UserRemoveDiscLinkEvent {

    private final Users unlinked;

    public UserRemoveDiscLinkEvent(Users unlinked) {
        this.unlinked = unlinked;
    }

    public Users getUnlinked() { return unlinked; }
}
