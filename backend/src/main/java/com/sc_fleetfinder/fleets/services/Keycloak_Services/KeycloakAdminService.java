package com.sc_fleetfinder.fleets.services.Keycloak_Services;

import com.sc_fleetfinder.fleets.events.UserRemoveDiscLinkEvent;

public interface KeycloakAdminService {

    public void deleteKeycloakUser(String kcId);

    //event listener
    public void removeDiscordAccountLink(UserRemoveDiscLinkEvent event);

    public String generateDiscordKeycloakLink(String token);
}
