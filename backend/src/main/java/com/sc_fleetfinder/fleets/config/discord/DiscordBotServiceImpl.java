package com.sc_fleetfinder.fleets.config.discord;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationRepository;
import com.sc_fleetfinder.fleets.entities.Notification;
import com.sc_fleetfinder.fleets.utils.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordBotServiceImpl implements DiscordBotService {

    private final RestClient discordRestClient;
    private final NotificationRepository notificationRepository;
    private final GroupListingRepository glr;

    @Override
    public void sendDiscordNotification(String discordUserId, Notification note) {
        String channelId = openDmChannel(discordUserId);
        ResponseEntity<?> response = sendDmNotification(channelId, note);

        if(response.getStatusCode() == HttpStatus.OK) {
            note.setReadAt(Instant.now());
        } else {
            String message = "Failed to send push notification for user with ID: [" + note.getUser().getUserId()
                    + "]. " + response.getBody();
            notificationRepository.delete(note);
            throw new ResponseStatusException(response.getStatusCode(), message);
        }
    }

    private String openDmChannel(String discordUserId) {
        Map<String, String> body = Map.of("recipient_id", discordUserId);

        Map response = discordRestClient.post()
                .uri("/users/@me/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
        return (String) response.get("id");
    }

    private ResponseEntity<?> sendDmNotification(String channelId, Notification note) {
        String field1;
        String value1;

        String field2;
        String value2;

        switch (note.getType()) {
            case NotificationType.NEW_LISTING_MATCH:
                field1 = "Group Status";
                value1 = note.getTargetMetadata().getAddContext();

                field2 = "Check it out: ";
                value2 = "https://scfleetfinder.com/listing-details/" + note.getTargetMetadata().getTargetId();

                break;
            case NotificationType.NEW_CHAT_MESSAGE:
                field1 = "Conversation: ";
                value1 = note.getTargetMetadata().getAddContext();

                field2 = "Check it out: ";
                value2 = "https://scfleetfinder.com/user-account";

                break;
            case NotificationType.NEW_GROUP_INVITE:
                field1 = "Group Status: ";
                value1 = glr.findById(note.getOutbox().getParentEntityId())
                        .map(g -> g.getGroupStatus().getGroupStatus())
                        .orElse(null);

                field2 = "Check it out: ";
                value2 = "https://scfleetfinder.com/user-account";
                break;
            case NotificationType.NEW_GROUP_MEMBER:
                field1 = "Role: ";
                value1 = note.getTargetMetadata().getTargetLabel();

                field2 = "Check it out: ";
                value2 = "https://scfleetfinder.com/user-account";
                break;
            case NotificationType.MOD_DELETE:
                field1 = "Action performed by a(n): ";
                value1 = note.getTargetMetadata().getTargetLabel();

                field2 = "Moderator note: ";
                value2 = note.getTargetMetadata().getAddContext();
                break;
            default:
                field1 = "New Status: ";
                value1 = note.getTargetMetadata().getAddContext();

                field2 = "You can visit the How To page to learn more about status meanings:";
                value2 = "https://scfleetfinder.com/user-account";
        }

        Map<String, Object> embed = Map.of(
                "title", note.getTitle(),
                "description", note.getMessage(),
                "color", 0x5865F2,
                "fields", List.of(
                        Map.of("name", field1, "value", value1, "inline", true),
                        Map.of("name", field2, "value", value2, "inline", true)
                ),
                "timestamp", Instant.now().toString()
        );
        Map<String, Object> body = Map.of(
                "embeds", List.of(embed)
        );

        return discordRestClient.post()
                .uri("/channels/{channelId}/messages", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
