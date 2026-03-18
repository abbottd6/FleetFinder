package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreatePushSubRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.UpdatePushSubRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetPushSubDto;
import com.sc_fleetfinder.fleets.config.SecurityConfig;
import com.sc_fleetfinder.fleets.controllers.NotificationPrefsAndPushSubController;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.CustomNotificationService;
import com.sc_fleetfinder.fleets.services.CRUD_services.PushSubscriptionService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationPrefsAndPushSubController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class NotificationPrefsAndPushSubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomNotificationService customNotificationService;

    @MockitoBean
    private PushSubscriptionService pushSubscriptionService;

    private Users mockUser;

    private static final String MOCK_KCID = "someKeycloakId";

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("TestUser");
        mockUser.setKeycloakId(MOCK_KCID);
        mockUser.setEmail("test@gmail.com");
        when(userService.verifyUser(MOCK_KCID)).thenReturn(mockUser);
    }

    // ─── GET /get_my_push_subs ────────────────────────────────────────────────

    @Test
    void testGetMyPushSubs_Success_Returns200() throws Exception {
        GetPushSubDto dto = new GetPushSubDto();
        dto.setIdPushSub(1L);
        Page<GetPushSubDto> page = new PageImpl<>(List.of(dto));

        when(pushSubscriptionService.getAllMyPushSubs(any(), any())).thenReturn(page);

        mockMvc.perform(post("/api/user_notification_preferences/get_my_push_subs")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pageIdx\":0,\"pageSize\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists());
    }

    @Test
    void testGetMyPushSubs_NoAuth_Returns401() throws Exception {
        mockMvc.perform(post("/api/user_notification_preferences/get_my_push_subs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pageIdx\":0,\"pageSize\":10}"))
                .andExpect(status().isUnauthorized());
    }

    // ─── POST /create_push_sub ────────────────────────────────────────────────

    @Test
    void testCreatePushSub_Success_Returns200() throws Exception {
        CreatePushSubRequestDto dto = new CreatePushSubRequestDto();
        dto.setUserLabel("My Device");
        dto.setDeviceUrl("https://push.example.com/sub/123");
        dto.setPublicKey("pubKey");
        dto.setBrowserSecret("secret");

        GetPushSubDto responseDto = new GetPushSubDto();
        responseDto.setIdPushSub(1L);

        when(pushSubscriptionService.createNewPushSub(any(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/user_notification_preferences/create_push_sub")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.idPushSub").value(1));
    }

    @Test
    void testCreatePushSub_NoAuth_Returns401() throws Exception {
        mockMvc.perform(post("/api/user_notification_preferences/create_push_sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userLabel\":\"test\",\"deviceUrl\":\"url\",\"publicKey\":\"key\",\"browserSecret\":\"secret\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreatePushSub_InvalidDto_Returns400() throws Exception {
        // blank deviceUrl fails @NotBlank validation → 400
        mockMvc.perform(post("/api/user_notification_preferences/create_push_sub")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userLabel\":\"test\",\"deviceUrl\":\"\",\"publicKey\":\"key\",\"browserSecret\":\"secret\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePushSub_DuplicateDevice_PropagatesIllegalArgumentException() throws Exception {
        // IllegalArgumentException has no global handler.
        // In @WebMvcTest (no ErrorPageFilter), it propagates from perform() as a root cause.
        CreatePushSubRequestDto dto = new CreatePushSubRequestDto();
        dto.setUserLabel("My Device");
        dto.setDeviceUrl("https://push.example.com/sub/123");
        dto.setPublicKey("pubKey");
        dto.setBrowserSecret("secret");

        when(pushSubscriptionService.createNewPushSub(any(), any()))
                .thenThrow(new IllegalArgumentException("Push subscription already exists for this user and device."));

        assertThatThrownBy(() ->
                mockMvc.perform(post("/api/user_notification_preferences/create_push_sub")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
        ).hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    // ─── PUT /update_push_sub ─────────────────────────────────────────────────

    @Test
    void testUpdatePushSub_Success_Returns200() throws Exception {
        UpdatePushSubRequestDto dto = new UpdatePushSubRequestDto();
        dto.setIdPushSub(1L);
        dto.setLabel("sysNotes");
        dto.setValue(true);

        GetPushSubDto responseDto = new GetPushSubDto();
        responseDto.setIdPushSub(1L);

        when(pushSubscriptionService.updatePushSub(any(), any())).thenReturn(responseDto);

        mockMvc.perform(put("/api/user_notification_preferences/update_push_sub")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists());
    }

    @Test
    void testUpdatePushSub_NoAuth_Returns401() throws Exception {
        mockMvc.perform(put("/api/user_notification_preferences/update_push_sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPushSub\":1,\"label\":\"sysNotes\",\"value\":true}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdatePushSub_NotOwner_Returns401() throws Exception {
        // ActionNotAuthorizedException has @ResponseStatus(HttpStatus.UNAUTHORIZED) → 401
        when(pushSubscriptionService.updatePushSub(any(), any()))
                .thenThrow(new ActionNotAuthorizedException(1L, "update", "PushSubscription", 5L));

        UpdatePushSubRequestDto dto = new UpdatePushSubRequestDto();
        dto.setIdPushSub(5L);
        dto.setLabel("sysNotes");
        dto.setValue(true);

        mockMvc.perform(put("/api/user_notification_preferences/update_push_sub")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdatePushSub_InvalidLabel_PropagatesIllegalArgumentException() throws Exception {
        // IllegalArgumentException has no global handler.
        // In @WebMvcTest (no ErrorPageFilter), it propagates from perform() as a root cause.
        when(pushSubscriptionService.updatePushSub(any(), any()))
                .thenThrow(new IllegalArgumentException("badLabel is not a valid notification category."));

        UpdatePushSubRequestDto dto = new UpdatePushSubRequestDto();
        dto.setIdPushSub(1L);
        dto.setLabel("badLabel");
        dto.setValue(true);

        assertThatThrownBy(() ->
                mockMvc.perform(put("/api/user_notification_preferences/update_push_sub")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
        ).hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    // ─── DELETE /delete_push_sub/{subId} ──────────────────────────────────────

    @Test
    void testDeletePushSub_Success_Returns200() throws Exception {
        when(pushSubscriptionService.deletePushSub(any(), eq(1L))).thenReturn(1);

        mockMvc.perform(delete("/api/user_notification_preferences/delete_push_sub/1")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(1));
    }

    @Test
    void testDeletePushSub_NoAuth_Returns401() throws Exception {
        mockMvc.perform(delete("/api/user_notification_preferences/delete_push_sub/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeletePushSub_NotFound_Returns404() throws Exception {
        when(pushSubscriptionService.deletePushSub(any(), eq(1L)))
                .thenThrow(new ResourceNotFoundException("PushSubscription", 1L, 1L));

        mockMvc.perform(delete("/api/user_notification_preferences/delete_push_sub/1")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isNotFound());
    }
}
