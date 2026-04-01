package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.config.ActivityTracking.UserActivityCache;
import com.sc_fleetfinder.fleets.config.SecurityConfig;
import com.sc_fleetfinder.fleets.controllers.BackgroundCleanupController;
import com.sc_fleetfinder.fleets.services.CRUD_services.BackgroundCRUD.BackgroundCleanupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BackgroundCleanupController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class BackgroundCleanupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BackgroundCleanupService cleanupService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserActivityCache userActivityCache;

    @Test
    void clickedListingsClean_WithIds_Returns200AndDelegatesToService() throws Exception {
        when(cleanupService.clickedListingsClean(any())).thenReturn(ResponseEntity.ok().build());

        Set<Long> ids = Set.of(1L, 2L, 3L);
        mockMvc.perform(put("/api/be-busy/clicked-clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk());

        verify(cleanupService).clickedListingsClean(any());
    }

    @Test
    void clickedListingsClean_WithEmptySet_Returns200() throws Exception {
        when(cleanupService.clickedListingsClean(any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(put("/api/be-busy/clicked-clean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());
    }
}
