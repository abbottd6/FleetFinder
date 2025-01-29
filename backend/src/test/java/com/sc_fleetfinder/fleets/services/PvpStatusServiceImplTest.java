package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PvpStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
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
class PvpStatusServiceImplTest {

    @Mock
    PvpStatusRepository pvpStatusRepository;

    @InjectMocks
    PvpStatusServiceImpl pvpStatusService;

    @Test
    void testGetAllPvpStatuses_Found() {
        //given
        PvpStatus mockPvpStatus1 = new PvpStatus();
        PvpStatus mockPvpStatus2 = new PvpStatus();
        List<PvpStatus> mockPvpStatuses = List.of(mockPvpStatus2, mockPvpStatus1);
        when(pvpStatusRepository.findAll()).thenReturn(mockPvpStatuses);

        //when
        List<PvpStatusDto> result = pvpStatusService.getAllPvpStatuses();

        //then
        assertAll("getAllPvpStatuses mock entity assertion set: ",
                () -> assertNotNull(result, "getAllPvpStatuses should not return null"),
                () -> assertEquals(2, result.size(), "expected getAllPvpStatuses to return 2 mock" +
                        " entities"),
                () -> verify(pvpStatusRepository, times(1)).findAll());
    }

    @Test
    void testGetAllPvpStatuses_NotFound() {
        //given
        when(pvpStatusRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<PvpStatusDto> result = pvpStatusService.getAllPvpStatuses();

        //then
        assertAll("getAllPvpStatuses = empty, assertion set: ",
                () -> assertNotNull(result, "getAllPvpStatuses should return empty, not null"),
                () -> assertTrue(result.isEmpty(), "getAllPvpStatuses returned " + result +
                        " when it was expected to be empty"),
                () -> verify(pvpStatusRepository, times(1)).findAll());
    }

    @Test
    void testGetPvpStatusById_Found() {
        //given
        PvpStatus mockPvpStatus = new PvpStatus();
        when(pvpStatusRepository.findById(1)).thenReturn(Optional.of(mockPvpStatus));


        //when
        PvpStatusDto result = pvpStatusService.getPvpStatusById(1);

        //then
        assertAll("getPvpStatusById = found assertion set: ",
                () -> assertNotNull(result, "getPvpStatusById should not return null when Id was expected to " +
                        "be found"),
                () -> assertDoesNotThrow(() -> pvpStatusService.getPvpStatusById(1),
                        "getPvpStatusById was expected to be found, should not have thrown an exception"),
                () -> verify(pvpStatusRepository, times(2)).findById(1));
    }

    @Test
    void testGetPvpStatusById_NotFound() {
        //given
        when(pvpStatusRepository.findById(1)).thenReturn(Optional.empty());

        //when pvpStatusRepository does not contain entity with given Id

        //then
        assertThrows(ResourceNotFoundException.class, () -> pvpStatusService.getPvpStatusById(1),
                "getPvpStatusById was expected to be not found, should have thrown exception");
    }
}