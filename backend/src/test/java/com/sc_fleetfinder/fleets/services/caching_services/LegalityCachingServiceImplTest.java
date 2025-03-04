package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.LegalityConversionServiceImpl;
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
class LegalityCachingServiceImplTest {

    @Mock
    private LegalityRepository legalityRepository;

    @Mock
    private LegalityConversionServiceImpl legalityConversionService;

    @InjectMocks
    private LegalityCachingServiceImpl legalityCachingService;

    @Test
    void testCacheAllLegalities_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(LegalityCachingServiceImpl.class);
        //given mock entities for find all
        Legality mockEntity1 = new Legality();
            mockEntity1.setLegalityId(1);
            mockEntity1.setLegalityStatus("Status1");
        Legality mockEntity2 = new Legality();
            mockEntity2.setLegalityId(2);
            mockEntity2.setLegalityStatus("Status2");
        List<Legality> mockLegalities = List.of(mockEntity1, mockEntity2);
        when(legalityRepository.findAll()).thenReturn(mockLegalities);

        //mock dto's to return from conversion in cacheAll
        LegalityDto mockDto1 = new LegalityDto();
            mockDto1.setLegalityId(1);
            mockDto1.setLegalityStatus("Status1");
        LegalityDto mockDto2 = new LegalityDto();
            mockDto2.setLegalityId(2);
            mockDto2.setLegalityStatus("Status2");
        when(legalityConversionService.convertToDto(mockEntity1)).thenReturn(mockDto1);
        when(legalityConversionService.convertToDto(mockEntity2)).thenReturn(mockDto2);

        //when
        List<LegalityDto> result = legalityCachingService.cacheAllLegalities();

        //then
        assertAll("cacheAllLegalities mock entities assertion set:",
                () -> assertNotNull(result, "cacheAllLegalities should not return null"),
                () -> assertEquals(2, result.size(), "cacheAllLegalities should return 2 mock entities"),
                () -> assertEquals(1, result.getFirst().getLegalityId(), "cacheAllLegalities " +
                        "produced a dto with incorrect id"),
                () -> assertEquals("Status1", result.getFirst().getLegalityStatus(),
                        "cacheAllLegalities produced a dto with the incorrect legality status"),
                () -> assertEquals(2, result.get(1).getLegalityId(), "cacheAllLegalities " +
                        "produced a dto with the incorrect id"),
                () -> assertEquals("Status2", result.get(1).getLegalityStatus(),
                "cacheAllLegalities produced a dto with the incorrect legality status"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful " +
                        "cacheAllLegalities should not produce any error logs."),
                () -> verify(legalityRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllLegalities_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(LegalityCachingServiceImpl.class);
        //given legality repo is empty

        //when
        //then
        assertAll("cacheAllLegalities = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> legalityCachingService.cacheAllLegalities()),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Unable to access legalities data for caching."))),
                () -> verify(legalityRepository, times(1)).findAll());
    }
}