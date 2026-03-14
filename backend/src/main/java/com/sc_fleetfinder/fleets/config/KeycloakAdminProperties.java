package com.sc_fleetfinder.fleets.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "keycloak.admin")
@Component
@Data
public class KeycloakAdminProperties {
    private String clientId;
    private String clientSecret;
    private String realm;
    private String serverUrl;
}
