package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerRegionServiceImplTest {

    @Mock
    ServerRegionRepository serverRegionRepository;

    @InjectMocks
    ServerRegionServiceImpl serverRegionService;

    @Test
    void testGetAllServerRegions_Found() {
        //given
        ServerRegion mockServer1 = new ServerRegion();
        ServerRegion mockServer2 = new ServerRegion();
        List<ServerRegion> mockServers = List.of(mockServer1, mockServer2);
        when(serverRegionRepository.findAll()).thenReturn(mockServers);

        //when
        List<ServerRegionDto> result = serverRegionService.getAllServerRegions();

        //then
        assertAll("getAllServerRegions entity assertion set: ",
                () -> assertNotNull(result, "getAllServerRegions was expected to contain 2 mock entities"),
                () -> assertEquals(2, result.size(), "getAllServerRegions should have contained " +
                        "2 mock entities"),
                () -> verify(serverRegionRepository, times(1)).findAll());
    }

    @Test
    void testGetAllServerRegions_NotFound() {
        //given
        when(serverRegionRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<ServerRegionDto> result = serverRegionService.getAllServerRegions();

        //then
        assertAll("getAllServerRegions = empty assertion set: ",
                () -> assertNotNull(result, "getAllServerRegions was expected to be empty, not null"),
                () -> assertTrue(result.isEmpty(), "getAllServerRegions returned " + result +
                        " when it was expected to be empty"),
                () -> verify(serverRegionRepository, times(1)).findAll());
    }

    @Test
    void testGetServerRegionById_Found() {
        //given
        ServerRegion mockServer = new ServerRegion();
        when(serverRegionRepository.findById(1)).thenReturn(Optional.of(mockServer));

        //when
        ServerRegionDto result = serverRegionService.getServerRegionById(1);

        //then
        assertAll("getServerRegionById = found assertion set: ",
                () -> assertNotNull(result, "getServerRegionById was expected to be found, not null"),
                () -> assertDoesNotThrow(() -> serverRegionService.getServerRegionById(1),
                        "getServerRegionById was expected to be found and should not throw an exception"),
                () -> verify(serverRegionRepository, times(2)).findById(1));
    }

    @Test
    void testGetServerRegionById() {
        //given
        when(serverRegionRepository.findById(1)).thenReturn(Optional.empty());

        //when serverRegionRepository does not contain entity with given Id

        //then
        assertThrows(ResourceNotFoundException.class, () -> serverRegionService.getServerRegionById(1),
        "getServerRegionById expected to be not found, should have thrown an exception");
    }
}