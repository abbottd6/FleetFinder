package com.sc_fleetfinder.fleets.services.Keycloak_Services;

import com.sc_fleetfinder.fleets.config.KeycloakAdminProperties;
import com.sc_fleetfinder.fleets.events.UserRemoveDiscLinkEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Base64;

@Service
@Slf4j
public class KeycloakAdminServiceImpl implements KeycloakAdminService {

    private final KeycloakAdminProperties props;
    private final RestTemplate restTemplate;
    private final Environment environment;

    public KeycloakAdminServiceImpl(KeycloakAdminProperties props, RestTemplate restTemplate, Environment environment) {
        this.props = props;
        this.restTemplate = restTemplate;
        this.environment = environment;
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

    @Override
    public void deleteKeycloakUser(String kcId) {
        String token = getAdminToken();
        String url = props.getServerUrl() + "/admin/realms/" + props.getRealm() + "/users/" + kcId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
    }

    @Override
    @SneakyThrows //Mac.getInstance may throw algorithm not found
    public String generateDiscordKeycloakLink(String sessionState) {
        String nonce = UUID.randomUUID().toString();

        String redirectUri = props.getFrontendBaseUrl() + "/user-account";
        String identityProvider = "discord";

        String combine = nonce + sessionState + props.getFrontendClientId() + identityProvider;

        log.info(combine);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] check = md.digest(combine.getBytes(StandardCharsets.UTF_8));
        String hash = Base64.getUrlEncoder().withoutPadding().encodeToString(check);

        return props.getServerUrl() + "/realms/" + props.getRealm() + "/broker/"
                + identityProvider + "/link?client_id=" + props.getFrontendClientId()
                + "&redirect_uri=" + redirectUri + "&nonce=" + nonce
                + "&hash=" + hash;
    }

    @Override
    @EventListener
    public void removeDiscordAccountLink(UserRemoveDiscLinkEvent event) {
        String token = getAdminToken();

        // use same header/token for both requests
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        String kcId = event.getUnlinked().getKeycloakId();

        // set the user's profile fields for discord to null
        try {
            String userUrl = props.getServerUrl() + "/admin/realms/" + props.getRealm() + "/users/" + kcId;

            //first must retrieve the profile from keycloak, otherwise it will wipe everything that is not
            // explicitly provided in the PUT
            ResponseEntity<Map> currentUser = restTemplate.exchange(
                    userUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            Map<String, Object> kcProfile = new HashMap<>(currentUser.getBody());

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("discord_user_id", null);
            attributes.put("discord_username", null);

            kcProfile.put("attributes", attributes);

            ResponseEntity<Void> response = restTemplate.exchange(userUrl, HttpMethod.PUT, new HttpEntity<>(kcProfile, headers), Void.class);
            log.info("Keycloak update status: {}, \n body: \n {}", response.getStatusCode(), response.getBody());
        }
        catch (Exception e) {
            log.error("""
                    User attempted to remove Discord link but keycloak failed to clear the attributes.\s
                     \
                    kcId: {}\s
                     error message:\s
                     {}""", kcId, e.getMessage());
        }

        //remove federated identity link for discord
        try {
            String fedUrl = props.getServerUrl() + "/admin/realms/" + props.getRealm() + "/users/"
                    + kcId + "/federated-identity/discord";
            ResponseEntity<Void> fedResponse = restTemplate.exchange(fedUrl, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
            log.info("fedResponse code: {} \n fedResponse body: {}", fedResponse.getStatusCode(), fedResponse.getBody());
        }
        catch (Exception e) {
            log.warn("User attempted to remove Discord link but a federated identity was not found" +
                    "for the user with kcId: {}, removal skipped.", kcId);
            log.error(e.getMessage());
        }
    }
}
