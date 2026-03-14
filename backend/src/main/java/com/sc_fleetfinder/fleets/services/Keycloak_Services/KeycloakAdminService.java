package com.sc_fleetfinder.fleets.services.Keycloak_Services;

import com.sc_fleetfinder.fleets.config.KeycloakAdminProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class KeycloakAdminService {

    private final KeycloakAdminProperties props;
    private final RestTemplate restTemplate;

    public KeycloakAdminService(KeycloakAdminProperties props, RestTemplate restTemplate) {
        this.props = props;
        this.restTemplate = restTemplate;
    }

    private String getAdminToken() {
        String url = props.getServerUrl() + "/realms/" + props.getRealm() + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", props.getClientId());
        body.add("client_secret", props.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    public void deleteKeycloakUser(String kcId) {
        String token = getAdminToken();
        String url = props.getServerUrl() + "/admin/realms/" + props.getRealm() + "/users/" + kcId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
    }
}
