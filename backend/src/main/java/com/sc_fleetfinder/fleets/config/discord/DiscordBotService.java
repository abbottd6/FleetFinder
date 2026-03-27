package com.sc_fleetfinder.fleets.config.discord;

import com.sc_fleetfinder.fleets.entities.Notification;

public interface DiscordBotService {
    void sendDiscordNotification(String discordUserId, Notification note);
}
