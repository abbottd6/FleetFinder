package com.sc_fleetfinder.fleets.unit_tests.websocket;

import com.sc_fleetfinder.fleets.messaging.websocket.JwtQueryParamHandshakeInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtQueryParamHandshakeInterceptorTest {

    @Mock private ServerHttpRequest mockRequest;
    @Mock private ServerHttpResponse mockResponse;
    @Mock private WebSocketHandler mockWsHandler;

    private JwtQueryParamHandshakeInterceptor interceptor;
    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        interceptor = new JwtQueryParamHandshakeInterceptor();
        attributes = new HashMap<>();
    }

    @Test
    void beforeHandshake_NoAccessToken_AttributesEmpty_ReturnsTrue() throws Exception {
        when(mockRequest.getURI()).thenReturn(new URI("ws://localhost/websocket"));

        boolean result = interceptor.beforeHandshake(mockRequest, mockResponse, mockWsHandler, attributes);

        assertThat(result).isTrue();
        assertThat(attributes).doesNotContainKey("token");
    }

    @Test
    void beforeHandshake_PlainToken_StoredAsIs_ReturnsTrue() throws Exception {
        when(mockRequest.getURI()).thenReturn(new URI("ws://localhost/websocket?access_token=abc123"));

        boolean result = interceptor.beforeHandshake(mockRequest, mockResponse, mockWsHandler, attributes);

        assertThat(result).isTrue();
        assertThat(attributes).containsEntry("token", "abc123");
    }

    @Test
    void beforeHandshake_TokenWithBearerPercentPrefix_StoredStripped_ReturnsTrue() throws Exception {
        // "Bearer%20" is the URL-encoded form of "Bearer " — the interceptor strips this prefix
        when(mockRequest.getURI()).thenReturn(new URI("ws://localhost/websocket?access_token=Bearer%20abc123"));

        boolean result = interceptor.beforeHandshake(mockRequest, mockResponse, mockWsHandler, attributes);

        assertThat(result).isTrue();
        assertThat(attributes).containsEntry("token", "abc123");
    }

    @Test
    void beforeHandshake_EmptyTokenParam_StoredInAttributes_ReturnsTrue() throws Exception {
        // An empty access_token param is non-null, so it is stored; the next layer (JwtSubHandshakeHandler) will reject it
        when(mockRequest.getURI()).thenReturn(new URI("ws://localhost/websocket?access_token="));

        boolean result = interceptor.beforeHandshake(mockRequest, mockResponse, mockWsHandler, attributes);

        assertThat(result).isTrue();
        assertThat(attributes).containsEntry("token", "");
    }

    @Test
    void afterHandshake_DoesNotThrow() {
        assertThatNoException().isThrownBy(() ->
                interceptor.afterHandshake(mockRequest, mockResponse, mockWsHandler, null));
    }
}
