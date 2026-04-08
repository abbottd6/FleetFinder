package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PublicUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.conversion_services.UserConversionServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
public class UserConversionServiceImplTest {

    @InjectMocks
    private UserConversionServiceImpl userConversionService;

    @Mock
    private ModelMapper modelMapper;

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void convertToPrivateDto_Success() {

        //given a new user to convert
        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setKeycloakId("mockKeycloakId");
        mockUser.setUsername("mockUsername");
        mockUser.setEmail("thisrawemail@gmail.com");
        mockUser.setIsDeleted(false);

        PrivateUserResponseDto mockDto = new PrivateUserResponseDto();
        mockDto.setUserId(1L);
        mockDto.setUsername("mockUsername");

        when(modelMapper.map(mockUser, PrivateUserResponseDto.class)).thenReturn(mockDto);

        Set<ConstraintViolation<PrivateUserResponseDto>> dtoConstraintViolations =
                validator.validate(userConversionService.convertToPrivateDto(mockUser));

        //when convert is called
        PrivateUserResponseDto dto = assertDoesNotThrow(
                () -> userConversionService.convertToPrivateDto(mockUser)
        );

        assertAll("users convertToDto assertion set: Success",
                () -> assertThat(dto).hasFieldOrProperty("acctCreated"),
                () -> assertThat(dto).hasFieldOrProperty("groupListingsDto"),
                () -> assertEquals(dto.getUsername(), mockUser.getUsername()),
                () -> assertTrue(dtoConstraintViolations.isEmpty(),
                        () -> "Expected no validation errors, but got: " + dtoConstraintViolations));
    }

    @Test
    void convertToPublicDto_Success() {

        //given a new user to convert
        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setKeycloakId("mockKeycloakId");
        mockUser.setUsername("mockUsername");
        mockUser.setEmail("thisrawemail@gmail.com");
        mockUser.setIsDeleted(false);
        mockUser.setInGameUsername("anInGameUsername");

        PublicUserResponseDto mockDto = new PublicUserResponseDto();
        mockDto.setUserId(1L);
        mockDto.setUsername("mockUsername");

        when(modelMapper.map(mockUser, PublicUserResponseDto.class)).thenReturn(mockDto);

        Set<ConstraintViolation<PublicUserResponseDto>> dtoConstraintViolations =
                validator.validate(userConversionService.convertToPublicDto(mockUser));

        //when convert is called
        PublicUserResponseDto dto = assertDoesNotThrow(
                () -> userConversionService.convertToPublicDto(mockUser)
        );

        // creating a set of fields to compare with expected
        // to protect against mixing of public and private dtos
        Set<String> actualFields = Arrays.stream(dto.getClass().getDeclaredFields())
                        .map(Field::getName)
                                .collect(Collectors.toSet());

        Set<String> expectedFields = Set.of(
                "userId",
                "username",
                "server",
                "org",
                "about",
                "inGameUsername"
        );

        assertAll("users convertToDto assertion set: Success",
                () -> assertEquals(actualFields, expectedFields),
                () -> assertEquals(dto.getUsername(), mockUser.getUsername()),
                () -> assertTrue(dtoConstraintViolations.isEmpty(),
                        () -> "Expected no validation errors, but got: " + dtoConstraintViolations));
    }

    @Test
    void convertToEntity_Failure() {
        //given a new user to convert
        Users mockUser = new Users();

        PrivateUserResponseDto mockDto = new PrivateUserResponseDto();
        mockDto.setUserId(null);
        mockDto.setUsername("");

        when(modelMapper.map(mockUser, PrivateUserResponseDto.class)).thenReturn(mockDto);

        Set<ConstraintViolation<PrivateUserResponseDto>> dtoConstraintViolations =
                validator.validate(userConversionService.convertToPrivateDto(mockUser));

        List<String> violationsList = dtoConstraintViolations.stream()
                        .map(violation -> violation.getPropertyPath().toString())
                                .distinct().toList();

        assertAll("users convertToDto assertion set: Failure",
                () -> assertFalse(dtoConstraintViolations.isEmpty(), "expected constraint violations"),
                () -> assertTrue(violationsList.contains("userId"), "null userId should be a violation"),
                () -> assertTrue(violationsList.contains("username"), "empty userName should be a violation"));
    }
}
