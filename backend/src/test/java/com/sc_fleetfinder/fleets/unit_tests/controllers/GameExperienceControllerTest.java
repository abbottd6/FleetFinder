package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.controllers.GameExperienceController;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GameExperienceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(controllers = GameExperienceController.class)
class GameExperienceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameExperienceService gameExperienceService;

    private List<GameExperienceDto> mockExperiences;

    @BeforeEach
    void setUp() {
        GameExperienceDto experienceDto1 = new GameExperienceDto();
            experienceDto1.setExperienceId(1);
            experienceDto1.setExperienceType("Persistent Universe");
        GameExperienceDto experienceDto2 = new GameExperienceDto();
            experienceDto2.setExperienceId(2);
            experienceDto2.setExperienceType("Arena Commander");

        mockExperiences = Arrays.asList(experienceDto1, experienceDto2);
    }

    @Test
    void testGetAllGameExperiences_FoundList() throws Exception {
        when(gameExperienceService.getAllExperiences()).thenReturn(mockExperiences);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameExperiences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].experienceId").value(1))
                .andExpect(jsonPath("$[0].experienceType").value("Persistent Universe"))
                .andExpect(jsonPath("$[1].experienceId").value(2))
                .andExpect(jsonPath("$[1].experienceType").value("Arena Commander"));
    }

    @Test
    void testGetAllGameExperiences_NotFound() throws Exception {
        when(gameExperienceService.getAllExperiences())
                .thenThrow(new ResourceNotFoundException("Unable to access data for game experiences"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameExperiences"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetGameExperienceById_Found() throws Exception {
        when(gameExperienceService.getExperienceById(1)).thenReturn(mockExperiences.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameExperiences/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.experienceId").value(1))
                .andExpect(jsonPath("$.experienceType").value("Persistent Universe"));
    }

    @Test
    void testGetGameExperienceById_NotFound() throws Exception {
        when(gameExperienceService.getExperienceById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameExperiences/33"))
                .andExpect(status().isNotFound());
    }

}