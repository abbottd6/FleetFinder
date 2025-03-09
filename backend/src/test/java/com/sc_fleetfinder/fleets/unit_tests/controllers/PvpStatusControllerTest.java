package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.controllers.PvpStatusController;
import com.sc_fleetfinder.fleets.services.CRUD_services.PvpStatusService;
import org.junit.jupiter.api.Test;

import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PvpStatusController.class)
class PvpStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PvpStatusService pvpStatusService;

    private List<PvpStatusDto> mockPvpStatuses;

    @BeforeEach
    void setUp() {
        PvpStatusDto mockPvpStatus1 = new PvpStatusDto();
        mockPvpStatus1.setPvpStatusId(1);
        mockPvpStatus1.setPvpStatus("PvP");
        PvpStatusDto mockPvpStatus2 = new PvpStatusDto();
        mockPvpStatus2.setPvpStatusId(2);
        mockPvpStatus2.setPvpStatus("PvE");

        mockPvpStatuses = Arrays.asList(mockPvpStatus1, mockPvpStatus2);
    }

    @Test
    void testGetAllPvpStatuses_FoundList() throws Exception {
        when(pvpStatusService.getAllPvpStatuses()).thenReturn(mockPvpStatuses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pvpStatuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pvpStatusId").value(1))
                .andExpect(jsonPath("$[0].pvpStatus").value("PvP"))
                .andExpect(jsonPath("$[1].pvpStatusId").value(2))
                .andExpect(jsonPath("$[1].pvpStatus").value("PvE"));
    }

    @Test
    void testGetAllPvpStatuses_NotFound() throws Exception {
        when(pvpStatusService.getAllPvpStatuses())
                .thenThrow(new ResourceNotFoundException("Unable to access PvP status data"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pvpStatuses"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPvpStatusById_Found() throws Exception {
        when(pvpStatusService.getPvpStatusById(1)).thenReturn(mockPvpStatuses.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pvpStatuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pvpStatusId").value(1))
                .andExpect(jsonPath("$.pvpStatus").value("PvP"));
    }

    @Test
    void testGetPvpStatusById_NotFound() throws Exception {
        when(pvpStatusService.getPvpStatusById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pvpStatuses/33"))
                .andExpect(status().isNotFound());
    }
}