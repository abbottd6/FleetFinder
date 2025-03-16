package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.controllers.GameplayCategoryController;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GameplayCategoryService;
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

@WebMvcTest(controllers = GameplayCategoryController.class)
class GameplayCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameplayCategoryService gameplayCategoryService;

    private List<GameplayCategoryDto> mockCategories;

    @BeforeEach
    void setUp() {
        GameplayCategoryDto mockCategoryDto1 = new GameplayCategoryDto();
            mockCategoryDto1.setGameplayCategoryId(1);
            mockCategoryDto1.setGameplayCategoryName("Ship Combat");
        GameplayCategoryDto mockCategoryDto2 = new GameplayCategoryDto();
            mockCategoryDto2.setGameplayCategoryId(2);
            mockCategoryDto2.setGameplayCategoryName("Medical");

        mockCategories = Arrays.asList(mockCategoryDto1, mockCategoryDto2);
    }

    @Test
    void testGetAllGameplayCategories_FoundList() throws Exception {
        when(gameplayCategoryService.getAllCategories()).thenReturn(mockCategories);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameplayCategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].gameplayCategoryId").value(1))
                .andExpect(jsonPath("$[0].gameplayCategoryName").value("Ship Combat"))
                .andExpect(jsonPath("$[1].gameplayCategoryId").value(2))
                .andExpect(jsonPath("$[1].gameplayCategoryName").value("Medical"));
    }

    @Test
    void testGetAllGameplayCategories_NotFound() throws Exception {
        when(gameplayCategoryService.getAllCategories())
                .thenThrow(new ResourceNotFoundException("Unable to access data for gameplay categories"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameplayCategories"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetGameplayCategoryById_Found() throws Exception {
        when(gameplayCategoryService.getCategoryById(1)).thenReturn(mockCategories.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameplayCategories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameplayCategoryId").value(1))
                .andExpect(jsonPath("$.gameplayCategoryName").value("Ship Combat"));
    }

    @Test void testGetGameplayCategoryById_NotFound() throws Exception {
        when(gameplayCategoryService.getCategoryById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameplayCategories/33"))
                .andExpect(status().isNotFound());
    }
}