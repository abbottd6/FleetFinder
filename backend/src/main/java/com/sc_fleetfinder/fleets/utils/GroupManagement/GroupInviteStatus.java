package com.sc_fleetfinder.fleets.utils.GroupManagement;

public enum GroupInviteStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    RESCINDED,
    LEFT_OR_REMOVED;

    public boolean isTerminal() {
        return this == RESCINDED || this == DECLINED || this == ACCEPTED || this == LEFT_OR_REMOVED;
    }
}

