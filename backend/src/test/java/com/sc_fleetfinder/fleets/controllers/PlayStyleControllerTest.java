package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.PlayStyleService;
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

@WebMvcTest(controllers = PlayStyleController.class)
class PlayStyleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayStyleService playStyleService;

    private List<PlayStyleDto> mockPlayStyles;

    @BeforeEach
    void setUp() {
        PlayStyleDto mockPlayStyleDto1 = new PlayStyleDto();
        mockPlayStyleDto1.setStyleId(1);
        mockPlayStyleDto1.setPlayStyle("Casual");
        PlayStyleDto mockPlayStyleDto2 = new PlayStyleDto();
        mockPlayStyleDto2.setStyleId(2);
        mockPlayStyleDto2.setPlayStyle("Competitive");

        mockPlayStyles = Arrays.asList(mockPlayStyleDto1, mockPlayStyleDto2);
    }

    @Test
    void testGetAllPlayStyles_FoundList() throws Exception {
        when(playStyleService.getAllPlayStyles()).thenReturn(mockPlayStyles);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/playStyles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].styleId").value(1))
                .andExpect(jsonPath("$[0].playStyle").value("Casual"))
                .andExpect(jsonPath("$[1].styleId").value(2))
                .andExpect(jsonPath("$[1].playStyle").value("Competitive"));
    }

    @Test
    void testGetAllPlayStyles_NotFound() throws Exception {
        when(playStyleService.getAllPlayStyles())
                .thenThrow(new ResourceNotFoundException("Unable to access play style data"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/playStyles"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPlayStyleById_Found()  throws Exception {
        when(playStyleService.getPlayStyleById(1)).thenReturn(mockPlayStyles.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/playStyles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.styleId").value(1))
                .andExpect(jsonPath("$.playStyle").value("Casual"));
    }

    @Test
    void testGetPlayStyleById_NotFound()  throws Exception {
        when(playStyleService.getPlayStyleById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/playStyles/33"))
                .andExpect(status().isNotFound());
    }
}