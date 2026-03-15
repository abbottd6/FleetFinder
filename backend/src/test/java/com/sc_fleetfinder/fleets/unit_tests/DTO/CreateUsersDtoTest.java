package com.sc_fleetfinder.fleets.unit_tests.DTO;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateUserDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateUsersDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCreateUserDto_requiredFieldsOnly() {
        CreateUserDto dto = new CreateUserDto();
        dto.setKeycloakId("some-keycloak-id");
        dto.setUsername("Batman");
        dto.setEmail("batman@example.com");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidCreateUserDto_withAllOptionalFields() {
        CreateUserDto dto = new CreateUserDto();
        dto.setKeycloakId("some-keycloak-id");
        dto.setUsername("Batman");
        dto.setEmail("batman@example.com");
        dto.setDiscordId("123456789012345");
        dto.setDiscordUsername("BatmanDiscord");
        dto.setServer("AUS");
        dto.setOrg("Justice League");
        dto.setAbout("I am Batman.");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidCreateUserDto_allNull() {
        CreateUserDto dto = new CreateUserDto();

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        // keycloakId (@NotNull), username (@NotBlank), email (@NotBlank)
        assertEquals(3, violations.size());
    }

    @Test
    void testInvalidCreateUserDto_nullKeycloakId() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("Batman");
        dto.setEmail("batman@example.com");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("keycloakId")));
    }

    @Test
    void testInvalidCreateUserDto_blankUsername() {
        CreateUserDto dto = new CreateUserDto();
        dto.setKeycloakId("some-keycloak-id");
        dto.setUsername("");
        dto.setEmail("batman@example.com");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void testInvalidCreateUserDto_usernameTooShort() {
        CreateUserDto dto = new CreateUserDto();
        dto.setKeycloakId("some-keycloak-id");
        dto.setUsername("ab"); // 2 chars, min is 3
        dto.setEmail("batman@example.com");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void testInvalidCreateUserDto_usernameTooLong() {
        CreateUserDto dto = new CreateUserDto();
        dto.setKeycloakId("some-keycloak-id");
        dto.setUsername("B".repeat(33)); // 33 chars, max is 32
        dto.setEmail("batman@example.com");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void testInvalidCreateUserDto_blankEmail() {
        CreateUserDto dto = new CreateUserDto();
        dto.setKeycloakId("some-keycloak-id");
        dto.setUsername("Batman");
        dto.setEmail("");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testInvalidCreateUserDto_invalidEmail() {
        CreateUserDto dto = new CreateUserDto();
        dto.setKeycloakId("some-keycloak-id");
        dto.setUsername("Batman");
        dto.setEmail("batman.com"); // missing @ sign

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testInvalidCreateUserDto_discordIdTooLong() {
        CreateUserDto dto = new CreateUserDto();
        dto.setKeycloakId("some-keycloak-id");
        dto.setUsername("Batman");
        dto.setEmail("batman@example.com");
        dto.setDiscordId("1".repeat(21)); // 21 chars, max is 20

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("discordId")));
    }

    @Test
    void testInvalidCreateUserDto_orgTooLong() {
        CreateUserDto dto = new CreateUserDto();
        dto.setKeycloakId("some-keycloak-id");
        dto.setUsername("Batman");
        dto.setEmail("batman@example.com");
        dto.setOrg("O".repeat(26)); // 26 chars, max is 25

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("org")));
    }

    @Test
    void testInvalidCreateUserDto_aboutTooLong() {
        CreateUserDto dto = new CreateUserDto();
        dto.setKeycloakId("some-keycloak-id");
        dto.setUsername("Batman");
        dto.setEmail("batman@example.com");
        dto.setAbout("A".repeat(256)); // 256 chars, max is 255

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("about")));
    }
}
