package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.controllers.LegalityController;
import com.sc_fleetfinder.fleets.services.CRUD_services.LegalityService;
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

@WebMvcTest(controllers = LegalityController.class)
class LegalityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LegalityService legalityService;

    private List<LegalityDto> mockLegalities;

    @BeforeEach
    void setUp() {
        LegalityDto mockLegalityDto1 = new LegalityDto();
        mockLegalityDto1.setLegalityId(1);
        mockLegalityDto1.setLegalityStatus("Lawful");
        LegalityDto mockLegalityDto2 = new LegalityDto();
        mockLegalityDto2.setLegalityId(2);
        mockLegalityDto2.setLegalityStatus("Unlawful");

        mockLegalities = Arrays.asList(mockLegalityDto1, mockLegalityDto2);
    }

    @Test
    void testGetAllLegalities_FoundList() throws Exception {
        when(legalityService.getAllLegalities()).thenReturn(mockLegalities);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/legalities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].legalityId").value(1))
                .andExpect(jsonPath("$[0].legalityStatus").value("Lawful"))
                .andExpect(jsonPath("$[1].legalityId").value(2))
                .andExpect(jsonPath("$[1].legalityStatus").value("Unlawful"));
    }

    @Test
    void testGetAllLegalities_NotFound() throws Exception {
        when(legalityService.getAllLegalities())
                .thenThrow(new ResourceNotFoundException("Unable to access data for legalities"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/legalities"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetLegalityById_Found() throws Exception {
        when(legalityService.getLegalityById(1)).thenReturn(mockLegalities.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/legalities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalityId").value(1))
                .andExpect(jsonPath("$.legalityStatus").value("Lawful"));
    }

    @Test
    void testGetLegalityById_NotFound() throws Exception {
        when(legalityService.getLegalityById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/legalities/33"))
                .andExpect(status().isNotFound());
    }
}