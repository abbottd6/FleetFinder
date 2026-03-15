package com.sc_fleetfinder.fleets.events;


import com.sc_fleetfinder.fleets.entities.Users;

public class UserAccountDeleteEvent {

    private final Users deleted;

    public UserAccountDeleteEvent(Users deleted) {
        this.deleted = deleted;
    }

    public Users getDeletedUser() { return deleted; }
}
