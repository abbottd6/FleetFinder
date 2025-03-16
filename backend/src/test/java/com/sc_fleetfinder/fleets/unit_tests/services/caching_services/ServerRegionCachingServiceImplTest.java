package com.sc_fleetfinder.fleets.unit_tests.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.ServerRegionCachingServiceImpl;
import com.sc_fleetfinder.fleets.services.conversion_services.ServerRegionConversionServiceImpl;
import nl.altindag.log.LogCaptor;
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
class ServerRegionCachingServiceImplTest {

    @InjectMocks
    private ServerRegionCachingServiceImpl serverRegionCachingService;

    @Mock
    private ServerRegionRepository serverRegionRepository;

    @Mock
    private ServerRegionConversionServiceImpl serverRegionConversionService;

    @Test
    void testCacheAllServerRegions_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(ServerRegionCachingServiceImpl.class);
        //given: mock entities for find all
        ServerRegion mockEntity1 = new ServerRegion();
            mockEntity1.setServerId(1);
            mockEntity1.setServerName("Server1");
        ServerRegion mockEntity2 = new ServerRegion();
            mockEntity2.setServerId(2);
            mockEntity2.setServerName("Server2");
        List<ServerRegion> mockEntities = List.of(mockEntity1, mockEntity2);
        when(serverRegionRepository.findAll()).thenReturn(mockEntities);

        //mock dto's to return from conversion in cacheAll
        ServerRegionDto mockDto1 = new ServerRegionDto();
            mockDto1.setServerId(1);
            mockDto1.setServername("Server1");
        ServerRegionDto mockDto2 = new ServerRegionDto();
            mockDto2.setServerId(2);
            mockDto2.setServername("Server2");
        when(serverRegionConversionService.convertToDto(mockEntity1)).thenReturn(mockDto1);
        when(serverRegionConversionService.convertToDto(mockEntity2)).thenReturn(mockDto2);

        //when
        List<ServerRegionDto> result = serverRegionCachingService.cacheAllServerRegions();

        //then
        assertAll("cacheAllServerRegions_Found assertion set: ",
                () -> assertNotNull(result, "successful cacheAllServerRegions should not return null"),
                () -> assertEquals(2, result.size(), "expected test cacheAllServerRegions to " +
                        "return a list containing 2 elements"),
                () -> assertEquals(1, result.getFirst().getServerId(), "cacheAllServerRegions " +
                        "produced a Dto with the incorrect Id."),
                () -> assertEquals("Server1", result.getFirst().getServername(),
                        "cacheAllServerRegions produced a Dto witht the incorrect server name."),
                () -> assertEquals(2, result.get(1).getServerId(), "cacheAllServerRegions " +
                        "produced a Dto with the incorrect Id."),
                () -> assertEquals("Server2", result.get(1).getServername(),
                        "cacheAllServerRegions produced a dto with the incorrect server name."),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful " +
                        "cacheAllServerRegions should not produce any error logs."),
                () -> verify(serverRegionRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllServerRegions_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(ServerRegionCachingServiceImpl.class);
        //given serverRegion repo is empty

        //when
        //then
        assertAll("cacheAllServerRegions_NotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        serverRegionCachingService.cacheAllServerRegions(), "cacheAllServerRegions should throw " +
                        "an exception when repo is empty."),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Unable to access server regions data for caching."))),
                () -> verify(serverRegionRepository, times(1)).findAll());
    }
}