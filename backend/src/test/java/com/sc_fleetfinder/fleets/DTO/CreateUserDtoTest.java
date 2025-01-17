package com.sc_fleetfinder.fleets.DTO;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateUserDto;
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
public class CreateUserDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    //Testing valid field values
    @Test
    public void testValidCreateUserDto() {
        CreateUserDto userDto = new CreateUserDto();
        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");
        userDto.setServer("AUS");
        userDto.setOrg("Organization");
        userDto.setAbout("I don't like bats.");

        //Act
        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidCreateUserDto_null() {
        CreateUserDto userDto = new CreateUserDto();

        //Act
        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());

        //number of fields with not null/empty/blank
        assertEquals(3, violations.size());
    }

    @Test
    public void testInvalidCreateUserDto_blankName() {
        CreateUserDto userDto = new CreateUserDto();

        userDto.setUsername("");
        userDto.setPassword("000000000");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Create user DTO field 'username' cannot be blank") | violation.getMessage().equals("Username must be between 1 and 32 characters.")));
    }

    @Test
    public void testInvalidCreateUserDto_blankPassword() {
        CreateUserDto userDto = new CreateUserDto();

        userDto.setUsername("Batman");
        userDto.setPassword("");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Create user DTO password cannot be blank") | violation.getMessage().equals("User password must be between 8 and 32 characters")));
    }

    @Test
    public void testInvalidCreateUserDto_blankEmail() {
        CreateUserDto userDto = new CreateUserDto();

        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("");

        //Act
        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Create user DTO field 'email' cannot be blank")));
    }

    @Test
    public void testValidCreateUserDto_validEmail() {
        CreateUserDto userDto = new CreateUserDto();

        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidCreateUserDto_invalidEmail() {
        CreateUserDto userDto = new CreateUserDto();

        userDto.setUsername("Batman");
        userDto.setPassword("123456789");
        userDto.setEmail("batman.com");

        //Act
        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidCreateUserDto_invalidPasswordMax() {
        CreateUserDto userDto = new CreateUserDto();

        userDto.setUsername("Batman");
        userDto.setPassword("123456789000000000000000000000000");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidCreateUserDto_invalidPasswordMin() {
        CreateUserDto userDto = new CreateUserDto();

        userDto.setUsername("Batman");
        userDto.setPassword("1234567");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidCreateUserDto_invalidUserNameMax() {
        CreateUserDto userDto = new CreateUserDto();

        userDto.setUsername("BatmanBigAndStrongAndToughAndBetterThanSpiderMan");
        userDto.setPassword("123456789");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }
}
