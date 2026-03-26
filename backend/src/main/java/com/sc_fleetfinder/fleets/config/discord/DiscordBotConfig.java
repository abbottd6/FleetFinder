package com.sc_fleetfinder.fleets.config.discord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class DiscordBotConfig {

    @Value("${discord.bot.token}")
    private String botToken;

    @Bean
    public RestClient discordRestClient() {
        return RestClient.builder()
                .baseUrl("https://discord.com/api/v10")
                .defaultHeader("Authorization", "Bot " + botToken)
                .build();
    }
}
