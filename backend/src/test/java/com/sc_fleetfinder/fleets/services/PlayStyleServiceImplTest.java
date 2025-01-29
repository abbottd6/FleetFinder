package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PlayStyleRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
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
class PlayStyleServiceImplTest {

    @Mock
    private PlayStyleRepository playStyleRepository;

    @InjectMocks
    private PlayStyleServiceImpl playStyleService;

    @Test
    void testGetAllPlayStyles_Found() {
        //given
        PlayStyle mockPlayStyle1 = new PlayStyle();
        PlayStyle mockPlayStyle2 = new PlayStyle();
        List<PlayStyle> mockPlayStyles = List.of(mockPlayStyle1, mockPlayStyle2);
        when(playStyleRepository.findAll()).thenReturn(mockPlayStyles);

        //when
        List<PlayStyleDto> result = playStyleService.getAllPlayStyles();

        //then
        assertAll("getAllPlayStyles mock entities assertion set: ",
                () -> assertNotNull(result, "getAllPlayStyles should not return null"),
                () -> assertEquals(2, result.size(), "getAllPlayStyles should return 2 mock entities"),
                () -> verify(playStyleRepository, times(1)).findAll());
    }

    @Test
    void testGetAllPlayStyles_NotFound() {
        //given
        when(playStyleRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<PlayStyleDto> result = playStyleService.getAllPlayStyles();

        //then
        assertAll("getAllPlayStyles = empty, assertion set: ",
                () -> assertNotNull(result, "getAllPlayStyles should return empty, not null"),
                () -> assertTrue(result.isEmpty(), "getAllPlayStyles returned " + result +
                        " when it should have returned empty"),
                () -> verify(playStyleRepository, times(1)).findAll());
    }

    @Test
    void testGetPlayStyleById_Found() {
        //given
        PlayStyle mockPlayStyle = new PlayStyle();
        when(playStyleRepository.findById(1)).thenReturn(Optional.of(mockPlayStyle));

        //when
        PlayStyleDto result = playStyleService.getPlayStyleById(1);

        //then
        assertAll("getPlayStyleById = found assertion set: ",
                () -> assertNotNull(result, "getPlayStyleById should not return null when expected found"),
                () -> assertDoesNotThrow(() -> playStyleService.getPlayStyleById(1),
                        "getPlayStyleById should not throw an exception when expected Id to be found"),
                () -> verify(playStyleRepository, times(2)).findById(1));
    }

    @Test
    void testGetPlayStyleById_NotFound() {
        //given
        when(playStyleRepository.findById(1)).thenReturn(Optional.empty());

        //when playStyleRepository does not contain entity with given Id

        //then
        assertThrows(ResourceNotFoundException.class, () -> playStyleService.getPlayStyleById(1),
                "getPlayStyleById expected to be not found, should throw an exception");
    }
}