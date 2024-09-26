package com.sc_fleetfinder.fleets.DTO;

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
public class UserDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    //Testing valid field values
    @Test
    public void testValidUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");
        userDto.setServer("AUS");
        userDto.setOrg("Organization");
        userDto.setAbout("Did you know that my mom and dad were murdered?");
        userDto.setAcctCreated(LocalDateTime.now());

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_null() {
        UserDto userDto = new UserDto();

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());

        //number of fields with not null/empty/blank
        assertEquals(4, violations.size());
    }

    @Test
    public void testInvalidUserDto_blankName() {
        UserDto userDto = new UserDto();

        userDto.setUserId(1L);
        userDto.setUsername("");
        userDto.setPassword("000000000");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Username is required")));
    }

    @Test
    public void testInvalidUserDto_blankPassword() {
        UserDto userDto = new UserDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Password is required")));
    }

    @Test
    public void testInvalidUserDto_blankEmail() {
        UserDto userDto = new UserDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("");

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Asert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Email is required")));
    }

    @Test
    public void testValidUserDto_validEmail() {
        UserDto userDto = new UserDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_invalidEmail() {
        UserDto userDto = new UserDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman.com");

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_invalidPasswordMax() {
        UserDto userDto = new UserDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789000000000000000000000000");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_invalidPasswordMin() {
        UserDto userDto = new UserDto();

        userDto.setUserId(1L);
        userDto.setUsername("Batman");
        userDto.setPassword("1234567");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_invalidUserNameMax() {
        UserDto userDto = new UserDto();

        userDto.setUserId(1L);
        userDto.setUsername("BatmanBigAndStrongAndToughAndBetterThanSpiderMan");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidUserDto_idNull() {
        UserDto userDto = new UserDto();

        userDto.setUserId(null);
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

}
