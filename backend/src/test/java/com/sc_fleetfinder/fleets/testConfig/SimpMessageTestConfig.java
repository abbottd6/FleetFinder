package com.sc_fleetfinder.fleets.testConfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ExecutorSubscribableChannel;

@TestConfiguration
public class SimpMessageTestConfig {
    @Bean
    SimpMessagingTemplate simpMessagingTemplate() {
        MessageChannel channel = new ExecutorSubscribableChannel();
        return new SimpMessagingTemplate(channel);
    }
}
