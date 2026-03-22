package com.sc_fleetfinder.fleets.unit_tests.websocket;

import com.sc_fleetfinder.fleets.messaging.websocket.JwtSubHandshakeHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.WebSocketHandler;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtSubHandshakeHandlerTest {

    @Mock private JwtDecoder jwtDecoder;
    @Mock private ServerHttpRequest mockRequest;
    @Mock private WebSocketHandler mockWsHandler;

    // Subclass to expose the protected determineUser() method for direct testing
    private static class TestablHandler extends JwtSubHandshakeHandler {
        TestablHandler(JwtDecoder decoder) { super(decoder); }

        public Principal callDetermineUser(ServerHttpRequest req, WebSocketHandler wsh, Map<String, Object> attrs) {
            return determineUser(req, wsh, attrs);
        }
    }

    private TestablHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestablHandler(jwtDecoder);
    }

    @Test
    void determineUser_NoTokenInAttributes_DelegatesToSuper_ReturnsNull() {
        Map<String, Object> attributes = new HashMap<>();
        // DefaultHandshakeHandler.determineUser() calls request.getPrincipal()
        when(mockRequest.getPrincipal()).thenReturn(null);

        Principal result = handler.callDetermineUser(mockRequest, mockWsHandler, attributes);

        assertThat(result).isNull();
        verify(jwtDecoder, never()).decode(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void determineUser_ValidToken_DecodesJwt_ReturnsPrincipalWithSub() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("token", "valid.jwt.token");

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn("user-sub-123");
        when(jwtDecoder.decode("valid.jwt.token")).thenReturn(mockJwt);

        Principal result = handler.callDetermineUser(mockRequest, mockWsHandler, attributes);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("user-sub-123");
        verify(jwtDecoder).decode("valid.jwt.token");
    }

    @Test
    void determineUser_InvalidJwt_BadJwtExceptionPropagates() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("token", "bad.token");
        when(jwtDecoder.decode("bad.token")).thenThrow(new BadJwtException("Invalid token"));

        assertThatThrownBy(() -> handler.callDetermineUser(mockRequest, mockWsHandler, attributes))
                .isInstanceOf(BadJwtException.class);
    }
}
