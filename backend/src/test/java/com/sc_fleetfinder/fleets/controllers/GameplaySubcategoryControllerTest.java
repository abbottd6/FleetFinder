package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.GameplaySubcategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GameplaySubcategoryController.class)
class GameplaySubcategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameplaySubcategoryService gameplaySubcategoryService;

    private List<GameplaySubcategoryDto> mockSubcategoryDtos;

    @BeforeEach
    void setUp() {
        GameplaySubcategoryDto mockSubcategoryDto1 = new GameplaySubcategoryDto();
        mockSubcategoryDto1.setSubcategoryId(1);
        mockSubcategoryDto1.setSubcategoryName("Dueling");
        mockSubcategoryDto1.setGameplayCategoryName("Ship Combat");
        GameplaySubcategoryDto mockSubcategoryDto2 = new GameplaySubcategoryDto();
        mockSubcategoryDto2.setSubcategoryId(2);
        mockSubcategoryDto2.setSubcategoryName("Beacon Response");
        mockSubcategoryDto2.setGameplayCategoryName("Medical");

        mockSubcategoryDtos = Arrays.asList(mockSubcategoryDto1, mockSubcategoryDto2);
    }

    @Test
    void testGetAllGameplaySubcategories_FoundList() throws Exception {
        when(gameplaySubcategoryService.getAllSubcategories()).thenReturn(mockSubcategoryDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameplaySubcategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].subcategoryId").value(1))
                .andExpect(jsonPath("$[0].subcategoryName").value("Dueling"))
                .andExpect(jsonPath("$[1].subcategoryId").value(2))
                .andExpect(jsonPath("$[1].subcategoryName").value("Beacon Response"));
    }

    @Test
    void testGetAllGameplaySubcategories_NotFound() throws Exception {
        when(gameplaySubcategoryService.getAllSubcategories())
                .thenThrow(new ResourceNotFoundException("Unable to access data for gameplay subcategories"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameplaySubcategories"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetGameplaySubcategoryById_Found() throws Exception {
        when(gameplaySubcategoryService.getSubcategoryById(1)).thenReturn(mockSubcategoryDtos.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameplaySubcategories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subcategoryId").value(1))
                .andExpect(jsonPath("$.subcategoryName").value("Dueling"));
    }

    @Test
    void testGetGameplaySubcategoryById_NotFound() throws Exception {
        when(gameplaySubcategoryService.getSubcategoryById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameplaySubcategoryById/33"))
                .andExpect(status().isNotFound());
    }
}