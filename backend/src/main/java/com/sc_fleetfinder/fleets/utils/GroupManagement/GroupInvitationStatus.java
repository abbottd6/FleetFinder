package com.sc_fleetfinder.fleets.utils.GroupManagement;

public enum GroupInvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    RESCINDED;

    public boolean isTerminal() {
        return this == RESCINDED || this == DECLINED || this == ACCEPTED;
    }
}

