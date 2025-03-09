package com.sc_fleetfinder.fleets.unit_tests.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.PvpStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PvpStatusCachingServiceImpl;
import com.sc_fleetfinder.fleets.services.conversion_services.PvpStatusConversionServiceImpl;
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
class PvpStatusCachingServiceImplTest {

    @Mock
    private PvpStatusRepository pvpStatusRepository;

    @Mock
    private PvpStatusConversionServiceImpl pvpStatusConversionService;

    @InjectMocks
    private PvpStatusCachingServiceImpl pvpStatusCachingService;

    @Test
    void testCacheAllPlayStyles_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(PvpStatusCachingServiceImpl.class);

        //given mock entities
        PvpStatus mockEntity1 = new PvpStatus();
            mockEntity1.setPvpStatusId(1);
            mockEntity1.setPvpStatus("Status1");
        PvpStatus mockEntity2 = new PvpStatus();
            mockEntity2.setPvpStatusId(2);
            mockEntity2.setPvpStatus("Status2");
        List<PvpStatus> mockEntities = List.of(mockEntity1, mockEntity2);
        when(pvpStatusRepository.findAll()).thenReturn(mockEntities);

        //mock dto's to return from conversion in cacheAll
        PvpStatusDto mockDto1 = new PvpStatusDto();
            mockDto1.setPvpStatusId(1);
            mockDto1.setPvpStatus("Status1");
        PvpStatusDto mockDto2 = new PvpStatusDto();
            mockDto2.setPvpStatusId(2);
            mockDto2.setPvpStatus("Status2");
        when(pvpStatusConversionService.convertToDto(mockEntity1)).thenReturn(mockDto1);
        when(pvpStatusConversionService.convertToDto(mockEntity2)).thenReturn(mockDto2);

        //when
        List<PvpStatusDto> result = pvpStatusCachingService.cacheAllPvpStatuses();

        //then
        assertAll("cacheAllPvpStatuses_Found assertion set: ",
                () -> assertNotNull(result, "successful cacheAllPvpStatuses should not return null."),
                () -> assertEquals(2, result.size(), "test cacheAllPvpStatuses expected to " +
                        "produce 2 dtos."),
                () -> assertEquals(1, result.getFirst().getPvpStatusId(), "cacheAllPvpStatuses " +
                        "produced a dto with the incorrect Id"),
                () -> assertEquals("Status1", result.getFirst().getPvpStatus(),
                        "cacheAllPvpStatuses produced a dto with the incorrect status name"),
                () -> assertEquals(2, result.get(1).getPvpStatusId(), "cacheAllPvpStatuses " +
                        "produced a dto with the incorrect id"),
                () -> assertEquals("Status2", result.get(1).getPvpStatus(),
                        "cacheAllPvpStatuses produced a dto with the incorrect status name"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful " +
                        "cacheAllPvpStatuses should not produce any error logs."),
                () -> verify(pvpStatusRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllPvpStatuses_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(PvpStatusCachingServiceImpl.class);
        //given pvp status repo is empty

        //when
        //then
        assertAll("cacheAllPvpStatuses_NotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        pvpStatusCachingService.cacheAllPvpStatuses()),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Unable to access pvp status data for caching"))),
                () -> verify(pvpStatusRepository, times(1)).findAll());
    }
}