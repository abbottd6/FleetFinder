package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupStatusService;
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

@WebMvcTest(controllers = GroupStatusController.class)
class GroupStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupStatusService groupStatusService;

    private List<GroupStatusDto> mockGroupStatuses;

    @BeforeEach
    void setUp() {
        GroupStatusDto mockGroupStatusDto1 = new GroupStatusDto();
        mockGroupStatusDto1.setGroupStatusId(1);
        mockGroupStatusDto1.setGroupStatus("Current/Live");
        GroupStatusDto mockGroupStatusDto2 = new GroupStatusDto();
        mockGroupStatusDto2.setGroupStatusId(2);
        mockGroupStatusDto2.setGroupStatus("Future/Scheduled");

        mockGroupStatuses = Arrays.asList(mockGroupStatusDto1, mockGroupStatusDto2);
    }

    @Test
    void testGetAllGroupStatuses_FoundList() throws Exception {
        when(groupStatusService.getAllGroupStatuses()).thenReturn(mockGroupStatuses);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/groupStatuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupStatusId").value(1))
                .andExpect(jsonPath("$[0].groupStatus").value("Current/Live"))
                .andExpect(jsonPath("$[1].groupStatusId").value(2))
                .andExpect(jsonPath("$[1].groupStatus").value("Future/Scheduled"));
    }

    @Test
    void testGetAllGroupStatuses_NotFound() throws Exception {
        when(groupStatusService.getAllGroupStatuses())
                .thenThrow(new ResourceNotFoundException("Unable to access group status data"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/groupStatuses"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetGroupStatusById_Found() throws Exception {
        when(groupStatusService.getGroupStatusById(1)).thenReturn(mockGroupStatuses.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/groupStatuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupStatusId").value(1))
                .andExpect(jsonPath("$.groupStatus").value("Current/Live"));
    }

    @Test
    void testGetGroupStatusById_NotFound() throws Exception {
        when(groupStatusService.getGroupStatusById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/groupStatuses/33"))
                .andExpect(status().isNotFound());
    }
}