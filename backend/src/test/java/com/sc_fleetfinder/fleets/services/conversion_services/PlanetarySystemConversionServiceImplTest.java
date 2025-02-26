package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetarySystemConversionServiceImplTest {

    @InjectMocks
    private PlanetarySystemConversionServiceImpl planetarySystemConversionService;

    @Mock
    private PlanetarySystemRepository planetarySystemRepository;

    @Test
    void testConvertToDto_Success() {
        PlanetarySystem mockEntity1 = new PlanetarySystem();
        mockEntity1.setSystemId(1);
        mockEntity1.setSystemName("System1");
        PlanetarySystem mockEntity2 = new PlanetarySystem();
        mockEntity2.setSystemId(2);
        mockEntity2.setSystemName("System2");

        //when
        List<PlanetarySystemDto> result = List.of(planetarySystemConversionService.convertToDto(mockEntity1),
                planetarySystemConversionService.convertToDto(mockEntity2));

        //then
        assertAll("Planetary system convertToDto: success assertion set ",
                () -> assertNotNull(result, "planetary system convertToDto should not return null"),
                () -> assertDoesNotThrow(() -> planetarySystemConversionService.convertToDto(mockEntity1),
                        "planetary system convertToDto should not throw an exception when input is valid"),
                () -> assertDoesNotThrow(() -> planetarySystemConversionService.convertToDto(mockEntity2),
                        "planetary system convertToDto should not throw an exception when input is valid"),
                () -> assertEquals(2, result.size(), "the test for planetarySystem covnertToDto is " +
                        "expected to produce 2 mock Dtos"),
                () -> assertEquals(1, result.getFirst().getSystemId(),
                        "planetarySystem convertToDto produced a Dto with an incorrect Id"),
                () -> assertEquals("System1", result.getFirst().getSystemName(),
                        "planetarySystem convertToDto produced a Dto with an incorrect system name"),
                () -> assertEquals(2, result.get(1).getSystemId(), "planetary system convertToDto " +
                        "produced a dto with an incorrect id"),
                () -> assertEquals("System2", result.get(1).getSystemName(), "planetary system convertToDto " +
                        "produced a dto with an incorrect system name"));
    }

    @Test
    void testConvertToDto_FailInvalid() {
        //given: entity with a null id value
        PlanetarySystem mockEntity1 = new PlanetarySystem();
        mockEntity1.setSystemId(null);
        mockEntity1.setSystemName("System1");
        mockEntity1.setPlanetMoonSystems(Set.of(new PlanetMoonSystem()));

        //entity with an id value of 0
        PlanetarySystem mockEntity2 = new PlanetarySystem();
        mockEntity2.setSystemId(0);
        mockEntity2.setSystemName("System2");
        mockEntity2.setPlanetMoonSystems(Set.of(new PlanetMoonSystem()));

        //entity with a null system name
        PlanetarySystem mockEntity3 = new PlanetarySystem();
        mockEntity3.setSystemId(3);
        mockEntity3.setSystemName(null);
        mockEntity3.setPlanetMoonSystems(Set.of(new PlanetMoonSystem()));
        //entity with an empty string as system name
        PlanetarySystem mockEntity4 = new PlanetarySystem();
        mockEntity4.setSystemId(4);
        mockEntity4.setSystemName("");
        mockEntity4.setPlanetMoonSystems(Set.of(new PlanetMoonSystem()));

        //when
        //then
        assertAll("planetarySystem convertToDto_FailInvalid assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> planetarySystemConversionService.convertToDto(mockEntity1),
                        "planetary system convertToDto should throw an exception when input id is null"),
                () -> assertThrows(ResourceNotFoundException.class, () -> planetarySystemConversionService.convertToDto(mockEntity2),
                        "planetary system convertToDto should throw an exception when input id is 0"),
                () -> assertThrows(ResourceNotFoundException.class, () -> planetarySystemConversionService.convertToDto(mockEntity3),
                        "planetary system convertToDto should throw an exception when system name is null"),
                () -> assertThrows(ResourceNotFoundException.class, () -> planetarySystemConversionService.convertToDto(mockEntity4),
                        "planetary system convertToDto should throw an exception when system name is empty string"));
    }

    @Test
    void testConvertToEntity_Found() {
        //given: a dto that matches an entity
        PlanetarySystemDto mockDto = new PlanetarySystemDto();
        mockDto.setSystemId(1);
        mockDto.setSystemName("System1");
        PlanetarySystem mockEntity = new PlanetarySystem();
        mockEntity.setSystemId(1);
        mockEntity.setSystemName("System1");
        when(planetarySystemRepository.findById(mockDto.getSystemId())).thenReturn(Optional.of(mockEntity));

        //when
        PlanetarySystem result = planetarySystemConversionService.convertToEntity(mockDto);

        //then
        assertAll("planetary system convertToEntity_Found assertion set: ",
                () -> assertNotNull(result, "planetary system convertToEntity expected to find a matching entity," +
                        " should not return null"),
                () -> assertDoesNotThrow(() -> planetarySystemConversionService.convertToEntity(mockDto),
                        "planetary system convertToEntity should not throw an exception when input dto is valid" +
                                "and found"),
                () -> assertEquals(1, result.getSystemId(), "planetary system covnertToEntity " +
                        "produced an entity with the incorrect ID"),
                () -> assertEquals("System1", result.getSystemName(), "planetary system convertToEntity " +
                        "produced an entity with the incorrect system name"),
                () -> assertSame(mockEntity, result, "planetary system convertToEntity was expected to match " +
                        "target entity but it did not"));
    }

    @Test
    void testConvertToEntity_FailInvalid() {
        //given
        //dto with an ID that does not match an existing entity
        PlanetarySystemDto mockDto = new PlanetarySystemDto();
        mockDto.setSystemId(1);
        mockDto.setSystemName("System1");
        assertThrows(ResourceNotFoundException.class, () -> planetarySystemConversionService.convertToEntity(mockDto),
                "planetary system convertToEntity should throw an exception when the id is not foudn in repo");

        //and given: dto with a name that does not match that of the entity with the same id
        PlanetarySystemDto mockDto2 = new PlanetarySystemDto();
        mockDto2.setSystemId(2);
        mockDto2.setSystemName("Incorrect System2");

        //and given: an entity with the same id as dto2 but a different name
        PlanetarySystem mockEntity = new PlanetarySystem();
        mockEntity.setSystemId(2);
        mockEntity.setSystemName("System2");
        when(planetarySystemRepository.findById(mockDto2.getSystemId())).thenReturn(Optional.of(mockEntity));

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> planetarySystemConversionService.convertToEntity(mockDto2),
                "planetary system convertToEntity should throw an exception when the dto name/id does " +
                        "not match the name of the entity with the same id");
    }
}