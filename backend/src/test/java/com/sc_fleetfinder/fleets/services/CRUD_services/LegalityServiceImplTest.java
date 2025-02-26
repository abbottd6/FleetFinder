package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.LegalityCachingServiceImpl;
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
class LegalityServiceImplTest {

    @Mock
    private LegalityRepository legalityRepository;

    @Mock
    private LegalityCachingServiceImpl legalityCachingService;

    @InjectMocks
    private LegalityServiceImpl legalityService;

    @Test
    void testGetAllLegalities_Found() {
        //given
        LegalityDto mockDto1 = new LegalityDto();
        LegalityDto mockDto2 = new LegalityDto();
        List<LegalityDto> mockLegalities = List.of(mockDto1, mockDto2);
        when(legalityCachingService.cacheAllLegalities()).thenReturn(mockLegalities);

        //when
        List<LegalityDto> result = legalityService.getAllLegalities();

        //then
        assertAll("getAllLegalities mock entities assertion set:",
                () -> assertNotNull(result, "getAllLegalities should not return null"),
                () -> assertEquals(2, result.size(), "getAllLegalities should return 2 mock entities"),
                () -> verify(legalityCachingService, times(1)).cacheAllLegalities());
    }

    @Test
    void testGetAllLegalities_NotFound() {
        //given: legality repo is empty

        //telling test to throw an exception when it tries to cache an empty repo
        when(legalityCachingService.cacheAllLegalities()).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        assertAll("getAllLegalities = empty, assertion set:",
                //verifying that the exception propagates from the caching service
                () -> assertThrows(ResourceNotFoundException.class, () -> legalityService.getAllLegalities()),
                () -> verify(legalityCachingService, times(1)).cacheAllLegalities());
    }

    @Test
    void testGetLegalityById_Found() {
        //given
        LegalityDto mockDto1 = new LegalityDto();
            mockDto1.setLegalityId(1);
            mockDto1.setLegalityStatus("Status1");
        LegalityDto mockDto2 = new LegalityDto();
            mockDto2.setLegalityId(2);
            mockDto2.setLegalityStatus("Status2");
        List<LegalityDto> mockLegalities = List.of(mockDto1, mockDto2);
        when(legalityCachingService.cacheAllLegalities()).thenReturn(mockLegalities);

        //when
        LegalityDto result = legalityService.getLegalityById(1);

        //then
        assertAll("getLegalityById = found, assertion set:",
                () -> assertNotNull(result, "getLegalityById should not return null when Id is found"),
                () -> assertDoesNotThrow(() -> legalityService.getLegalityById(1), "getLegalityById " +
                        "should not throw an exception when the Id is found"),
                () -> verify(legalityCachingService, times(2)).cacheAllLegalities());
    }

    @Test
    void testGetLegalityById_NotFound() {
        //given: legality repository does not contain entity with the given id

        //when
        //then
        assertAll("getLegalityById = not found, assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> legalityService.getLegalityById(1),
                        "getLegalityById with given Id not found should throw an exception"),
                () -> verify(legalityCachingService, times(1)).cacheAllLegalities());
    }
}