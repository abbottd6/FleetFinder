package com.sc_fleetfinder.fleets.unit_tests.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.PlayStyleRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PlayStyleCachingServiceImpl;
import com.sc_fleetfinder.fleets.services.conversion_services.PlayStyleConversionServiceImpl;
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

@ExtendWith({MockitoExtension.class})
class PlayStyleCachingServiceImplTest {

    @Mock
    private PlayStyleRepository playStyleRepository;

    @Mock
    private PlayStyleConversionServiceImpl playStyleConversionService;

    @InjectMocks
    private PlayStyleCachingServiceImpl playStyleCachingService;

    @Test
    void testCacheAllPlayStyles_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(PlayStyleCachingServiceImpl.class);
        //given mock entities
        PlayStyle mockEntity1 = new PlayStyle();
            mockEntity1.setStyleId(1);
            mockEntity1.setPlayStyle("Style1");
        PlayStyle mockEntity2 = new PlayStyle();
            mockEntity2.setStyleId(2);
            mockEntity2.setPlayStyle("Style2");
        List<PlayStyle> mockEntities = List.of(mockEntity1, mockEntity2);
        when(playStyleRepository.findAll()).thenReturn(mockEntities);

        //mock dto's to return from conversion in cacheAll
        PlayStyleDto mockDto1 = new PlayStyleDto();
            mockDto1.setStyleId(1);
            mockDto1.setPlayStyle("Style1");
        PlayStyleDto mockDto2 = new PlayStyleDto();
            mockDto2.setStyleId(2);
            mockDto2.setPlayStyle("Style2");
        when(playStyleConversionService.convertToDto(mockEntity1)).thenReturn(mockDto1);
        when(playStyleConversionService.convertToDto(mockEntity2)).thenReturn(mockDto2);

        //when
        List<PlayStyleDto> result = playStyleCachingService.cacheAllPlayStyles();

        //then
        assertAll("cacheAllPlayStyles_Found assertion set: ",
                () -> assertNotNull(result, "successful cacheAllPlayStyles should not return null"),
                () -> assertEquals(2, result.size(), "test cacheAllPlayStyles expected 2 dto's in list" +
                        " produced"),
                () -> assertEquals(1, result.getFirst().getStyleId(), "cacheAllPlayStyles produced a" +
                        " dto with the incorrect id"),
                () -> assertEquals("Style1", result.getFirst().getPlayStyle(), "cacheAllPlayStyles produced a" +
                        " result with the incorrect style name"),
                () -> assertEquals(2, result.get(1).getStyleId(), "cacheAllPlayStyles produced a " +
                        "result with the incorrect id"),
                () -> assertEquals("Style2", result.get(1).getPlayStyle(), "cacheAllPlayStyles produced " +
                        "a result with the incorrect style name"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful cacheAll should " +
                        "not produce any error logs"),
                () -> verify(playStyleRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllPlayStyles_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(PlayStyleCachingServiceImpl.class);
        //given play style repo is empty

        //when
        //then
        assertAll("cacheAllPlayStyles = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> playStyleCachingService.cacheAllPlayStyles()),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Unable to access play style data for caching."))),
                () -> verify(playStyleRepository, times(1)).findAll());
    }
}