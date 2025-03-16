package com.sc_fleetfinder.fleets.unit_tests.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.PlayStyleServiceImpl;
import com.sc_fleetfinder.fleets.services.caching_services.PlayStyleCachingServiceImpl;
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
class PlayStyleServiceImplTest {

    @Mock
    private PlayStyleCachingServiceImpl playStyleCachingService;

    @InjectMocks
    private PlayStyleServiceImpl playStyleService;

    @Test
    void testGetAllPlayStyles_Found() {
        //given
        PlayStyleDto mockPlayStyle1 = new PlayStyleDto();
        PlayStyleDto mockPlayStyle2 = new PlayStyleDto();
        List<PlayStyleDto> mockPlayStyles = List.of(mockPlayStyle1, mockPlayStyle2);
        when(playStyleCachingService.cacheAllPlayStyles()).thenReturn(mockPlayStyles);

        //when
        List<PlayStyleDto> result = playStyleService.getAllPlayStyles();

        //then
        assertAll("getAllPlayStyles mock entities assertion set: ",
                () -> assertNotNull(result, "getAllPlayStyles should not return null"),
                () -> assertEquals(2, result.size(), "getAllPlayStyles should return 2 mock entities"),
                () -> verify(playStyleCachingService, times(1)).cacheAllPlayStyles());
    }

    @Test
    void testGetAllPlayStyles_NotFound() {
        //given: play style repo is empty

        //telling test to throw an exception when it tries to cache empty repo
        when(playStyleCachingService.cacheAllPlayStyles()).thenThrow(ResourceNotFoundException.class);

        //then
        assertAll("getAllPlayStyles = empty, assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> playStyleService.getAllPlayStyles()),
                () -> verify(playStyleCachingService, times(1)).cacheAllPlayStyles());
    }

    @Test
    void testGetPlayStyleById_Found() {
        //given: mock play style dtos to return from cache all
        PlayStyleDto mockDto1 = new PlayStyleDto();
            mockDto1.setStyleId(1);
            mockDto1.setPlayStyle("Style1");
        PlayStyleDto mockDto2 = new PlayStyleDto();
            mockDto2.setStyleId(2);
            mockDto2.setPlayStyle("Style2");
        List<PlayStyleDto> mockPlayStyles = List.of(mockDto1, mockDto2);
        when(playStyleCachingService.cacheAllPlayStyles()).thenReturn(mockPlayStyles);

        //when
        PlayStyleDto result = playStyleService.getPlayStyleById(1);

        //then
        assertAll("getPlayStyleById = found assertion set: ",
                () -> assertNotNull(result, "getPlayStyleById should not return null when expected found"),
                () -> assertDoesNotThrow(() -> playStyleService.getPlayStyleById(1),
                        "getPlayStyleById should not throw an exception when expected Id to be found"),
                () -> assertEquals(1, result.getStyleId(), "getPlayStyleById produced a dto with " +
                        "the incorrect id"),
                () -> assertEquals("Style1", result.getPlayStyle(), "getPlayStyleById produced a " +
                        "dto with the incorrect style name"),
                () -> verify(playStyleCachingService, times(2)).cacheAllPlayStyles());
    }

    @Test
    void testGetPlayStyleById_NotFound() {
        //given: playStyleRepository does not contain the given id

        //when
        //then
        assertAll("getPlayStyleById = not found assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> playStyleService.getPlayStyleById(1),
                        "getPlayStyleById expected to be not found, should throw an exception"),
                () -> verify(playStyleCachingService, times(1)).cacheAllPlayStyles());
    }
}