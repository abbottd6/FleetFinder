package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PvpStatusCachingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PvpStatusServiceImplTest {

    @Mock
    PvpStatusCachingServiceImpl pvpStatusCachingService;

    @InjectMocks
    PvpStatusServiceImpl pvpStatusService;

    @Test
    void testGetAllPvpStatuses_Found() {
        //given
        PvpStatusDto mockPvpStatus1 = new PvpStatusDto();
        PvpStatusDto mockPvpStatus2 = new PvpStatusDto();
        List<PvpStatusDto> mockPvpStatuses = List.of(mockPvpStatus2, mockPvpStatus1);
        when(pvpStatusCachingService.cacheAllPvpStatuses()).thenReturn(mockPvpStatuses);

        //when
        List<PvpStatusDto> result = pvpStatusService.getAllPvpStatuses();

        //then
        assertAll("getAllPvpStatuses mock entity assertion set: ",
                () -> assertNotNull(result, "getAllPvpStatuses should not return null"),
                () -> assertEquals(2, result.size(), "expected getAllPvpStatuses to return 2 mock" +
                        " entities"),
                () -> verify(pvpStatusCachingService, times(1)).cacheAllPvpStatuses());
    }

    @Test
    void testGetAllPvpStatuses_NotFound() {
        //given
        when(pvpStatusCachingService.cacheAllPvpStatuses()).thenReturn(Collections.emptyList());

        //when
        List<PvpStatusDto> result = pvpStatusService.getAllPvpStatuses();

        //then
        assertAll("getAllPvpStatuses = empty, assertion set: ",
                () -> assertNotNull(result, "getAllPvpStatuses should return empty, not null"),
                () -> assertTrue(result.isEmpty(), "getAllPvpStatuses returned " + result +
                        " when it was expected to be empty"),
                () -> verify(pvpStatusCachingService, times(1)).cacheAllPvpStatuses());
    }

    @Test
    void testGetPvpStatusById_Found() {
        //given: mock pvp status dtos to return in cache all
        PvpStatusDto mockDto1 = new PvpStatusDto();
            mockDto1.setPvpStatusId(1);
            mockDto1.setPvpStatus("Status1");
        PvpStatusDto mockDto2 = new PvpStatusDto();
            mockDto2.setPvpStatusId(2);
            mockDto2.setPvpStatus("Status2");
        List<PvpStatusDto> mockStatusDtoes = List.of(mockDto1, mockDto2);

        when(pvpStatusCachingService.cacheAllPvpStatuses()).thenReturn(mockStatusDtoes);

        //when
        PvpStatusDto result = pvpStatusService.getPvpStatusById(1);

        //then
        assertAll("getPvpStatusById = found assertion set: ",
                () -> assertNotNull(result, "getPvpStatusById should not return null when Id was expected to " +
                        "be found"),
                () -> assertDoesNotThrow(() -> pvpStatusService.getPvpStatusById(1),
                        "getPvpStatusById was expected to be found, should not have thrown an exception"),
                () -> assertEquals(1, result.getPvpStatusId(), "testGetPvpStatusById " +
                        "produced a dto with the incorrect id."),
                () ->  assertEquals("Status1", result.getPvpStatus(), "testGetPvpStatusById " +
                        "produced a dto with the incorrect status name."),
                () -> verify(pvpStatusCachingService, times(2)).cacheAllPvpStatuses());
    }

    @Test
    void testGetPvpStatusById_NotFound() {
        //given: pvpStatusRepository does not contain entity with the given id

        //when
        //then
        assertAll("getPvpStatusById_NotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> pvpStatusService.getPvpStatusById(1),
                        "testGetPvpStatusById expected to not find the dto with this id, so it " +
                                "should have thrown an exception"),
                () -> verify(pvpStatusCachingService, times(1)).cacheAllPvpStatuses());
    }
}