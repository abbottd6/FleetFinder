package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GetNotificationDto;
import com.sc_fleetfinder.fleets.config.SecurityConfig;
import com.sc_fleetfinder.fleets.controllers.NotificationController;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.NotificationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class NotificationControllerTest {

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
    private NotificationService notificationService;

    private Users mockUser;

    private static final String MOCK_KCID = "notificationCtrlTestKcId";

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("NoteCtrlTestUser");
        mockUser.setKeycloakId(MOCK_KCID);
        when(userService.verifyUser(MOCK_KCID)).thenReturn(mockUser);
    }

    // ─── POST /my_notifications ───────────────────────────────────────────────

    @Test
    void testGetMyNotifications_Success_Returns200() throws Exception {
        GetNotificationDto dto = new GetNotificationDto();
        dto.setNotificationId(1L);
        Page<GetNotificationDto> page = new PageImpl<>(List.of(dto));

        when(notificationService.getMyNotifications(any(), any())).thenReturn(page);

        mockMvc.perform(post("/api/notify/my_notifications")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pageIdx\":0,\"pageSize\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void testGetMyNotifications_NoAuth_Returns401() throws Exception {
        mockMvc.perform(post("/api/notify/my_notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pageIdx\":0,\"pageSize\":10}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetMyNotifications_EmptyPage_Returns200() throws Exception {
        when(notificationService.getMyNotifications(any(), any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(post("/api/notify/my_notifications")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pageIdx\":0,\"pageSize\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    // ─── DELETE /delete/{noteId} ──────────────────────────────────────────────

    @Test
    void testDeleteNotification_Success_Returns200() throws Exception {
        doNothing().when(notificationService).deleteNotification(any(), eq(1L));

        mockMvc.perform(delete("/api/notify/delete/1")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true))
                .andExpect(jsonPath("$.noteId").value(1));
    }

    @Test
    void testDeleteNotification_NoAuth_Returns401() throws Exception {
        mockMvc.perform(delete("/api/notify/delete/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteNotification_NotFound_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Notification", 1L))
                .when(notificationService).deleteNotification(any(), eq(1L));

        mockMvc.perform(delete("/api/notify/delete/1")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNotification_NotOwner_Returns401() throws Exception {
        doThrow(new ActionNotAuthorizedException(1L, "deletion", "Notification", 1L))
                .when(notificationService).deleteNotification(any(), eq(1L));

        mockMvc.perform(delete("/api/notify/delete/1")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isUnauthorized());
    }

    // ─── DELETE /delete_all ───────────────────────────────────────────────────

    @Test
    void testDeleteAllNotifications_Success_Returns200() throws Exception {
        when(notificationService.deleteAllNotifications(any())).thenReturn(3);

        mockMvc.perform(delete("/api/notify/delete_all")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true))
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    void testDeleteAllNotifications_NoAuth_Returns401() throws Exception {
        mockMvc.perform(delete("/api/notify/delete_all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteAllNotifications_ZeroDeleted_Returns200() throws Exception {
        when(notificationService.deleteAllNotifications(any())).thenReturn(0);

        mockMvc.perform(delete("/api/notify/delete_all")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true))
                .andExpect(jsonPath("$.count").value(0));
    }
}
