package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
    void convertToDto_Success() {

        //given a new user to convert
        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setKeycloakId("mockKeycloakId");
        mockUser.setUsername("mockUsername");
        mockUser.setEmail("thisrawemail@gmail.com");
        mockUser.setIsDeleted(false);

        PrivateUserResponseDto mockDto = new PrivateUserResponseDto();
        mockDto.setUserId(1L);
        mockDto.setKeycloakId("mockKeycloakId");
        mockDto.setUsername("mockUsername");
        mockDto.setEmail("thisrawemail@gmail.com");

        when(modelMapper.map(mockUser, PrivateUserResponseDto.class)).thenReturn(mockDto);

        Set<ConstraintViolation<PrivateUserResponseDto>> dtoConstraintViolations =
                validator.validate(userConversionService.convertToDto(mockUser));

        //when convert is called
        PrivateUserResponseDto dto = assertDoesNotThrow(
                () -> userConversionService.convertToDto(mockUser)
        );

        assertAll("users convertToDto assertion set: Success",
                () -> assertEquals(dto.getUsername(), mockUser.getUsername()),
                () -> assertEquals(dto.getKeycloakId(), mockUser.getKeycloakId()),
                () -> assertEquals(dto.getEmail(), mockUser.getEmail()),
                () -> assertTrue(dtoConstraintViolations.isEmpty(),
                        () -> "Expected no validation errors, but got: " + dtoConstraintViolations));
    }

    @Test
    void convertToEntity_Failure() {
        //given a new user to convert
        Users mockUser = new Users();

        PrivateUserResponseDto mockDto = new PrivateUserResponseDto();
        mockDto.setUserId(null);
        mockDto.setKeycloakId("");
        mockDto.setUsername("");
        mockDto.setEmail("not a real email");

        when(modelMapper.map(mockUser, PrivateUserResponseDto.class)).thenReturn(mockDto);

        Set<ConstraintViolation<PrivateUserResponseDto>> dtoConstraintViolations =
                validator.validate(userConversionService.convertToDto(mockUser));

        List<String> violationsList = dtoConstraintViolations.stream()
                        .map(violation -> violation.getPropertyPath().toString())
                                .distinct()
                                        .collect(Collectors.toList());

        assertAll("users convertToDto assertion set: Failure",
                () -> assertFalse(dtoConstraintViolations.isEmpty(), "expected constraint violations"),
                () -> assertTrue(violationsList.contains("userId"), "null userId should be a violation"),
                () -> assertTrue(violationsList.contains("keycloakId"), "empty keycloakId should be a violation"),
                () -> assertTrue(violationsList.contains("username"), "empty userName should be a violation"),
                () -> assertTrue(violationsList.contains("email"), "invalid email format should be a violation"));
    }
}
