package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LegalityServiceImplTest {

    @Mock
    private LegalityRepository legalityRepository;

    @InjectMocks
    private LegalityServiceImpl legalityService;

    @Test
    void testGetAllLegalities_Found() {
        //given
        Legality mockLegality1 = new Legality();
        Legality mockLegality2 = new Legality();
        List<Legality> mockLegalities = List.of(mockLegality1, mockLegality2);
        when(legalityRepository.findAll()).thenReturn(mockLegalities);

        //when
        List<LegalityDto> result = legalityService.getAllLegalities();

        //then
        assertAll("getAllLegalities mock entities assertion set:",
                () -> assertNotNull(result, "getAllLegalities should not return null"),
                () -> assertEquals(2, result.size(), "getAllLegalities should return 2 mock entities"),
                () -> verify(legalityRepository, times(1)).findAll());
    }

    @Test
    void testGetAllLegalities_NotFound() {
        //given
        when(legalityRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<LegalityDto> result = legalityService.getAllLegalities();

        //then
        assertAll("get all legalities = empty, assertion set:",
                () -> assertNotNull(result, "getAllLegalities should be empty, not null"),
                () -> assertTrue(result.isEmpty(), "getAllLegalities returned " + result + "when it should " +
                        "have returned empty"),
                () -> verify(legalityRepository, times(1)).findAll());
    }

    @Test
    void testGetLegalityById_Found() {
        //given
        Legality mockLegality1 = new Legality();
        when(legalityRepository.findById(1)).thenReturn(Optional.of(mockLegality1));

        //when
        LegalityDto result = legalityService.getLegalityById(1);

        //then
        assertAll("getLegalityById = found, assertion set:",
                () -> assertNotNull(result, "getLegalityById should not return null when Id is found"),
                () -> assertDoesNotThrow(() -> legalityService.getLegalityById(1), "getLegalityById " +
                        "should not throw an exception when the Id is found"),
                () -> verify(legalityRepository, times(2)).findById(1));
    }

    @Test
    void testGetLegalityById_NotFound() {
        //given
        when(legalityRepository.findById(1)).thenReturn(Optional.empty());

        //when legalityRepository does not contain legality with given Id

        //then
        assertAll("get legalityById = not found, assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> legalityService.getLegalityById(1),
                        "getLegalityById with given Id not found should throw an exception"));
    }
}