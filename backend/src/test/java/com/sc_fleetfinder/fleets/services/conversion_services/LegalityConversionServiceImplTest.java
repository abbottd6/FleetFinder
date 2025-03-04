package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LegalityConversionServiceImplTest {

    @InjectMocks
    private LegalityConversionServiceImpl legalityConversionService;

    @Mock
    private LegalityRepository legalityRepository;

    @Test
    void testConvertToDto_Success() {
        LogCaptor logCaptor = LogCaptor.forClass(LegalityConversionServiceImpl.class);
        //given
        Legality mockEntity1 = new Legality();
        mockEntity1.setLegalityId(1);
        mockEntity1.setLegalityStatus("Status1");
        Legality mockEntity2 = new Legality();
        mockEntity2.setLegalityId(2);
        mockEntity2.setLegalityStatus("Status2");

        //when
        List<LegalityDto> result = List.of(legalityConversionService.convertToDto(mockEntity1),
                legalityConversionService.convertToDto(mockEntity2));

        //then
        assertAll("Legality convertToDto assertion set: ",
                () -> assertNotNull(result, "legality convertToDto should not return a null dto"),
                () -> assertDoesNotThrow(() -> legalityConversionService.convertToDto(mockEntity1),
                        "legality convertToDto should NOT throw an exception when fields are valid"),
                () -> assertDoesNotThrow(() -> legalityConversionService.convertToDto(mockEntity2),
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
                        "legality convertToDto produced a dto with the incorrect legality status"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful Legality " +
                        "convertToDto should not produce any error logs."));
    }

    @Test
    void testConvertToDto_FailInvalid() {
        LogCaptor logCaptor = LogCaptor.forClass(LegalityConversionServiceImpl.class);
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
                () -> assertThrows(ResourceNotFoundException.class, () ->
                                legalityConversionService.convertToDto(mockEntity1), "legality convertToDto " +
                                "should throw an exception when the id is null"),
                () -> assertThrows(ResourceNotFoundException.class, () ->
                                legalityConversionService.convertToDto(mockEntity2), "legality convertToDto " +
                                "should throw an exception when the id is 0"),
                () -> assertThrows(ResourceNotFoundException.class, () ->
                                legalityConversionService.convertToDto(mockEntity3), "legality convertToDto " +
                                "should throw an exception when the legality status is null"),
                () -> assertThrows(ResourceNotFoundException.class, () ->
                                legalityConversionService.convertToDto(mockEntity4), "legality convertToDto " +
                                "should throw an exception when the legality status is an empty string"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or zero legality id when converting from " +
                                "entity to Dto"))),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or empty status when converting from entity " +
                                "to Dto"))));
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
        Legality result = legalityConversionService.convertToEntity(mockDto);

        //then
        assertAll("legality convertToEntity assertion set: ",
                () -> assertNotNull(result, "legality convertToEntity should not return null"),
                () -> assertDoesNotThrow(() -> legalityConversionService.convertToEntity(mockDto),
                        "legality convertToEntity should not throw an exception when fields are valid"),
                () -> assertEquals(1, result.getLegalityId(), "legality convertToEntity " +
                        "produced an entity with the incorrect ID"),
                () -> assertEquals("Status1", result.getLegalityStatus(), "legality convertToEntity " +
                        "produced an entity with the incorrect legality status"),
                () -> assertSame(mockEntity, result, "legality convertToEntity does not match target entity"));
    }

    @Test
    void testConvertToEntity_FailInvalid() {
        LogCaptor logCaptor = LogCaptor.forClass(LegalityConversionServiceImpl.class);
        //given: dto that does not match a backend entity
        LegalityDto mockDto = new LegalityDto();
        //the id does not exist
        mockDto.setLegalityId(1);
        mockDto.setLegalityStatus("Not Legality1");
        assertThrows(ResourceNotFoundException.class, () -> legalityConversionService.convertToEntity(mockDto),
                "legality convertToEntity should throw an exception when the id is not found in repo");

        //given: dto with a name that does not match the name of the entity with the same id
        LegalityDto mockDto2 = new LegalityDto();
        mockDto2.setLegalityId(2);
        mockDto2.setLegalityStatus("Incorrect Legality");

        //mock entity for verifying an exception is thrown when id's match but names do not
        Legality mockEntity = new Legality();
        mockEntity.setLegalityId(2);
        mockEntity.setLegalityStatus("Correct Legality");
        when(legalityRepository.findById(mockDto2.getLegalityId())).thenReturn(Optional.of(mockEntity));

        //when
        //then
        assertAll("Legality convertToEntity_Fail_Invalid assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () ->
                        legalityConversionService.convertToEntity(mockDto2), "Legality convertToEntity " +
                        "should throw an exception when dto legality status does not match the status name of the " +
                        "entity with the same id."),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered legality with status name and Id mismatch: " +
                                "Incorrect Legality, when converting from Dto to entity"))));
    }
}