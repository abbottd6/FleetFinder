package com.sc_fleetfinder.fleets.unit_tests.websocket;

import com.sc_fleetfinder.fleets.messaging.websocket.StompJwtChannelInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StompJwtChannelInterceptorTest {

    @Mock private JwtDecoder jwtDecoder;
    @Mock private MessageChannel mockChannel;

    private StompJwtChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        // @Profile("!test") has no effect in pure Mockito tests — instantiate directly
        interceptor = new StompJwtChannelInterceptor(jwtDecoder);
    }

    // ─── Helper builders ──────────────────────────────────────────────────────

    private static Message<byte[]> buildConnectMessage(String authHeaderValue) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        if (authHeaderValue != null) {
            accessor.setNativeHeader("Authorization", authHeaderValue);
        }
        // setLeaveMutable(true) is REQUIRED: without it, MessageHeaders are frozen after
        // construction and accessor.setUser() in preSend throws IllegalStateException.
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private static Message<byte[]> buildSendMessage() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    @Test
    void preSend_NullAccessor_ReturnsOriginalMessageUnmodified() {
        // A message built without StompHeaderAccessor produces a null accessor lookup
        Message<byte[]> rawMessage = MessageBuilder.withPayload(new byte[0]).build();

        Message<?> result = interceptor.preSend(rawMessage, mockChannel);

        assertThat(result).isSameAs(rawMessage);
        verify(jwtDecoder, never()).decode(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void preSend_NonConnectFrame_PassesThrough_NoJwtCheck() {
        Message<byte[]> sendMessage = buildSendMessage();

        Message<?> result = interceptor.preSend(sendMessage, mockChannel);

        assertThat(result).isNotNull();
        verify(jwtDecoder, never()).decode(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void preSend_ConnectFrame_ValidBearerToken_SetsJwtAuthUserOnAccessor() {
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn("sub-123");
        when(jwtDecoder.decode("valid.token")).thenReturn(mockJwt);

        Message<byte[]> connectMessage = buildConnectMessage("Bearer valid.token");

        Message<?> result = interceptor.preSend(connectMessage, mockChannel);

        StompHeaderAccessor resultAccessor = MessageHeaderAccessor.getAccessor(result, StompHeaderAccessor.class);
        assertThat(resultAccessor).isNotNull();
        assertThat(resultAccessor.getUser()).isInstanceOf(JwtAuthenticationToken.class);
        assertThat(((JwtAuthenticationToken) resultAccessor.getUser()).getName()).isEqualTo("sub-123");
    }

    @Test
    void preSend_ConnectFrame_NullAuthorizationHeader_ThrowsUnauthorized() {
        // With the && → || fix applied, a null Authorization header now correctly throws
        // ResponseStatusException(UNAUTHORIZED) instead of NullPointerException.
        Message<byte[]> connectMessage = buildConnectMessage(null);

        assertThatThrownBy(() -> interceptor.preSend(connectMessage, mockChannel))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void preSend_ConnectFrame_NonBearerAuthHeader_ThrowsUnauthorized() {
        // With the && → || fix applied, a non-Bearer Authorization header now correctly throws
        // ResponseStatusException(UNAUTHORIZED) from the format check instead of bypassing it.
        Message<byte[]> connectMessage = buildConnectMessage("Basic dXNlcjpwYXNz");

        assertThatThrownBy(() -> interceptor.preSend(connectMessage, mockChannel))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void preSend_ConnectFrame_InvalidJwtToken_BadJwtExceptionPropagates() {
        when(jwtDecoder.decode("bad.token")).thenThrow(new BadJwtException("Expired"));
        Message<byte[]> connectMessage = buildConnectMessage("Bearer bad.token");

        assertThatThrownBy(() -> interceptor.preSend(connectMessage, mockChannel))
                .isInstanceOf(BadJwtException.class);
    }
}
