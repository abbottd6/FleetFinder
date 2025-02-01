package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.LegalityCachingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Test
    void testConvertToDto_Success() {
        //given
        Legality mockEntity1 = new Legality();
            mockEntity1.setLegalityId(1);
            mockEntity1.setLegalityStatus("Status1");
        Legality mockEntity2 = new Legality();
            mockEntity2.setLegalityId(2);
            mockEntity2.setLegalityStatus("Status2");

        //when
        List<LegalityDto> result = List.of(legalityService.convertToDto(mockEntity1),
                legalityService.convertToDto(mockEntity2));

        assertAll("Legality convertToDto assertion set: ",
                () -> assertNotNull(result, "legality convertToDto should not return a null dto"),
                () -> assertDoesNotThrow(() -> legalityService.convertToDto(mockEntity1),
                        "legality convertToDto should NOT throw an exception when fields are valid"),
                () -> assertDoesNotThrow(() -> legalityService.convertToDto(mockEntity2),
                        "legality convertToDto should NOT throw an exception when fields are valid"),
                () -> assertEquals(2, result.size(), "test legality convertToDto should produce " +
                        "2 mock dto's"),
                () -> assertEquals(1, result.getFirst().getLegalityId(), "legality convertToDto " +
                        "produced a dto with the incorrect ID"),
                () -> assertEquals("Status1", result.getFirst().getLegalityStatus(),
                        "legality convertToDto produced a dto with the incorrect legality status"),
                () -> assertEquals(2, result.get(1).getLegalityId(), "legality convertToDto " +
                        "produced a dto with the incorrect id"),
                () -> assertEquals("Status2", result.get(1).getLegalityStatus(),
                        "legality convertToDto produced a dto with the incorrect legality status"));
    }

    @Test
    void testConvertToDto_FailInvalid() {
        //given: entity with a null id value (invalid id)
        Legality mockEntity1 = new Legality();
            mockEntity1.setLegalityId(null);
            mockEntity1.setLegalityStatus("Lawful");
        //given: an entity with an id value 0
        Legality mockEntity2 = new Legality();
            mockEntity2.setLegalityId(0);
            mockEntity2.setLegalityStatus("Unlawful");
        //given: an entity with a null status value
        Legality mockEntity3 = new Legality();
            mockEntity3.setLegalityId(1);
            mockEntity3.setLegalityStatus(null);
        //given: an entity with an empty string status value
        Legality mockEntity4 = new Legality();
            mockEntity4.setLegalityId(2);
            mockEntity4.setLegalityStatus("");

        //when
        //then
        assertAll("legality convertToDto fail invalid assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> legalityService.convertToDto(mockEntity1),
                        "legality convertToDto should throw an exception when the id is null"),
                () -> assertThrows(ResourceNotFoundException.class, () -> legalityService.convertToDto(mockEntity2),
                        "legality convertToDto should throw an exception when the id is 0"),
                () -> assertThrows(ResourceNotFoundException.class, () -> legalityService.convertToDto(mockEntity3),
                        "legality convertToDto should throw an exception when the legality status is null"),
                () -> assertThrows(ResourceNotFoundException.class, () -> legalityService.convertToDto(mockEntity4),
                        "legality convertToDto should throw an exception when the legality status is " +
                                "an empty string"));
    }

    @Test
    void testConvertToEntity_Found() {
        //given: a dto that matches an entity
        LegalityDto mockDto = new LegalityDto();
            mockDto.setLegalityId(1);
            mockDto.setLegalityStatus("Status1");
        Legality mockEntity = new Legality();
            mockEntity.setLegalityId(1);
            mockEntity.setLegalityStatus("Status1");
        when(legalityRepository.findById(mockDto.getLegalityId())).thenReturn(Optional.of(mockEntity));

        //when
        Legality result = legalityService.convertToEntity(mockDto);

        //then
        assertAll("legality convertToEntity assertion set: ",
                () -> assertNotNull(result, "legality convertToEntity should not return null"),
                () -> assertDoesNotThrow(() -> legalityService.convertToEntity(mockDto),
                        "legality convertToEntity should not throw an exception when fields are valid"),
                () -> assertEquals(1, result.getLegalityId(), "legality convertToEntity " +
                        "produced an entity with the incorrect ID"),
                () -> assertEquals("Status1", result.getLegalityStatus(), "legality convertToEntity " +
                        "produced an entity with the incorrect legality status"),
                () -> assertSame(mockEntity, result, "legality convertToEntity does not match target entity"));
    }

    @Test
    void testConvertToEntity_FailInvalid() {
        //given: dto that does not match a backend entity
        LegalityDto mockDto = new LegalityDto();
            mockDto.setLegalityId(1);
            mockDto.setLegalityStatus("Not Legality1");
        assertThrows(ResourceNotFoundException.class, () -> legalityService.convertToEntity(mockDto),
                "legality convertToEntity should throw an exception when the id is not found in repo");

        //given: dto with a name that does not match the name of the entity with the same id
        LegalityDto mockDto2 = new LegalityDto();
            mockDto2.setLegalityId(2);
            mockDto2.setLegalityStatus("Incorrect Legality");

        Legality mockEntity = new Legality();
            mockEntity.setLegalityId(2);
            mockEntity.setLegalityStatus("Correct Legality");
        when(legalityRepository.findById(mockDto2.getLegalityId())).thenReturn(Optional.of(mockEntity));

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> legalityService.convertToEntity(mockDto2),
                "legality convertToEntity should throw an exception when the dto name/id does" +
                         " not match the name/id of the entity in repo");
    }
}