package com.sc_fleetfinder.fleets.DTO;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.UserRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.UserResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    //Testing valid field values
    @Test
    public void testValidRequestUserDto() {
        UserRequestDto userDto = new UserRequestDto();
        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");
        userDto.setServer("AUS");
        userDto.setOrg("Organization");
        userDto.setAbout("Did you know that my mom and dad were murdered?");
        userDto.setAcctCreated(LocalDateTime.now());

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_null() {
        UserRequestDto userDto = new UserRequestDto();

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());

        //number of fields with not null/empty/blank
        assertEquals(4, violations.size());
    }

    @Test
    public void testInvalidUserDto_blankName() {
        UserRequestDto userDto = new UserRequestDto();

        userDto.setUserId(1L);
        userDto.setUsername("");
        userDto.setPassword("000000000");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Username cannot be blank") | violation.getMessage().equals("Username must be between 1 and 32 characters.")));
    }

    @Test
    public void testInvalidUserDto_blankPassword() {
        UserRequestDto userDto = new UserRequestDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("User password cannot be blank") | violation.getMessage().equals("User password must be between 8 and 32 characters")));
    }

    @Test
    public void testInvalidUserDto_blankEmail() {
        UserRequestDto userDto = new UserRequestDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("");

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("User email cannot be blank")));
    }

    @Test
    public void testValidUserDto_validEmail() {
        UserRequestDto userDto = new UserRequestDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_invalidEmail() {
        UserRequestDto userDto = new UserRequestDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman.com");

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_invalidPasswordMax() {
        UserRequestDto userDto = new UserRequestDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789000000000000000000000000");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_invalidPasswordMin() {
        UserRequestDto userDto = new UserRequestDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("1234567");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_invalidUserNameMax() {
        UserRequestDto userDto = new UserRequestDto();

        userDto.setUserId(1L);
        userDto.setUsername("BatmanBigAndStrongAndToughAndBetterThanSpiderMan");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_idNull() {
        UserRequestDto userDto = new UserRequestDto();

        userDto.setUserId(null);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

}
