package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.ServerRegionCachingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerRegionServiceImplTest {

    @Mock
    private ServerRegionCachingServiceImpl serverRegionCachingService;

    @InjectMocks
    private ServerRegionServiceImpl serverRegionService;

    @Test
    void testGetAllServerRegions_Found() {
        //given
        ServerRegionDto mockDto1 = new ServerRegionDto();
        ServerRegionDto mockDto2 = new ServerRegionDto();
        List<ServerRegionDto> mockServerDtoes = List.of(mockDto1, mockDto2);
        when(serverRegionCachingService.cacheAllServerRegions()).thenReturn(mockServerDtoes);

        //when
        List<ServerRegionDto> result = serverRegionService.getAllServerRegions();

        //then
        assertAll("getAllServerRegions entity assertion set: ",
                () -> assertNotNull(result, "getAllServerRegions was expected to contain 2 mock entities"),
                () -> assertEquals(2, result.size(), "getAllServerRegions should have contained " +
                        "2 mock entities"),
                () -> verify(serverRegionCachingService, times(1)).cacheAllServerRegions());
    }

    @Test
    void testGetAllServerRegions_NotFound() {
        //given: server region repo is empty
        //then throw an exception if the cache all is requested
        when(serverRegionCachingService.cacheAllServerRegions()).thenThrow(ResourceNotFoundException.class);

        //then
        assertAll("getAllServerRegions = empty assertion set: ",
                //verifying that the exception propagates from the caching service
                () -> assertThrows(ResourceNotFoundException.class, () -> serverRegionService.getAllServerRegions()),
                () -> verify(serverRegionCachingService, times(1)).cacheAllServerRegions());
    }

    @Test
    void testGetServerRegionById_Found() {
        //given
        ServerRegionDto mockDto1 = new ServerRegionDto();
            mockDto1.setServerId(1);
            mockDto1.setServername("Server1");
        ServerRegionDto mockDto2 = new ServerRegionDto();
            mockDto2.setServerId(2);
            mockDto2.setServername("Server2");
        List<ServerRegionDto> mockServerDtoes = List.of(mockDto1, mockDto2);
        when(serverRegionCachingService.cacheAllServerRegions()).thenReturn(mockServerDtoes);

        //when
        ServerRegionDto result = serverRegionService.getServerRegionById(1);

        //then
        assertAll("getServerRegionById = found assertion set: ",
                () -> assertNotNull(result, "getServerRegionById was expected to be found, not null"),
                () -> assertDoesNotThrow(() -> serverRegionService.getServerRegionById(1),
                        "getServerRegionById was expected to be found and should not throw an exception"),
                () -> assertEquals("Server1", result.getServername(), "getServerRegionById " +
                        "returned the wrong server name."),
                () -> verify(serverRegionCachingService, times(2)).cacheAllServerRegions());
    }

    @Test
    void testGetServerRegionById_NotFound() {
        //given
        //when serverRegionRepository does not contain entity with given Id

        //then
        assertAll("getServerRegionById_NotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> serverRegionService.getServerRegionById(1),
                        "getServerRegionById expected to be not found, should have thrown an exception"),
                () -> verify(serverRegionCachingService, times(1)).cacheAllServerRegions());
    }
}