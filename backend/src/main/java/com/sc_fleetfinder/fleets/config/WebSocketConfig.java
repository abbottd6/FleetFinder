package com.sc_fleetfinder.fleets.config;

import com.sc_fleetfinder.fleets.messaging.websocket.StompJwtChannelInterceptor;
import com.sc_fleetfinder.fleets.messaging.websocket.StompSubscriptionAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.List;

import static com.sc_fleetfinder.fleets.constants.ConfigConstants.SPRING_SEC_WS_CONFIG_PRECEDENCE_OFFSET;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + SPRING_SEC_WS_CONFIG_PRECEDENCE_OFFSET)
@Profile("!test")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //WebSockets allowed origins is base url
    @Value("${app.frontend-base-url}")
    private String wsAppUrl;

    private final StompJwtChannelInterceptor stompJwtChannelInterceptor;
    private final StompSubscriptionAuthInterceptor stompSubscriptionAuthInterceptor;
    private final ApplicationContext applicationContext;
    private final AuthorizationManager<Message<?>> authorizationManager;

    public WebSocketConfig(ApplicationContext applicationContext, StompJwtChannelInterceptor stompJwtChannelInterceptor,
                           StompSubscriptionAuthInterceptor stompSubscriptionAuthInterceptor) {
        this.applicationContext = applicationContext;
        this.stompJwtChannelInterceptor = stompJwtChannelInterceptor;
        this.stompSubscriptionAuthInterceptor = stompSubscriptionAuthInterceptor;
        this.authorizationManager = messageAuthorizationManager();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(256 * 1024);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        AuthorizationChannelInterceptor authZ = new AuthorizationChannelInterceptor(authorizationManager);
        authZ.setAuthorizationEventPublisher(new SpringAuthorizationEventPublisher(applicationContext));

        registration.interceptors(
                stompJwtChannelInterceptor,
                new SecurityContextChannelInterceptor(),
                authZ,
                stompSubscriptionAuthInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/websocket")
                .setAllowedOriginPatterns(wsAppUrl);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //client -> server messages
        registry.setApplicationDestinationPrefixes("/app");

        //server -> client broadcasts
        registry.enableSimpleBroker("/topic", "/queue");

        //enables /user/queue/** routing
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    private AuthorizationManager<Message<?>> messageAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder messages =
                MessageMatcherDelegatingAuthorizationManager.builder();
        messages
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.DISCONNECT,
                        SimpMessageType.UNSUBSCRIBE).permitAll()
                .anyMessage().authenticated();
        return messages.build();
    }
}
