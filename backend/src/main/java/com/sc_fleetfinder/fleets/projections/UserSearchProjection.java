package com.sc_fleetfinder.fleets.projections;

import java.time.Instant;

public interface UserSearchProjection {
    Long getUserId();
    String getUsername();
    String getDiscordUsername();
    String getInGameUsername();
    Instant getLastAccess();
}
