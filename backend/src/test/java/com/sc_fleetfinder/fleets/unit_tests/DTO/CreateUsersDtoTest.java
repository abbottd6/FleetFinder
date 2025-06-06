package com.sc_fleetfinder.fleets.unit_tests.DTO;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateOrUpdateUserDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Disabled
public class CreateUsersDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    //Testing valid field values
    @Test
    public void testValidCreateUserDto() {
        CreateOrUpdateUserDto userDto = new CreateOrUpdateUserDto();
        userDto.setUsername("Batman");
        userDto.setEmail("batman@gmail.com");
        userDto.setServer("AUS");
        userDto.setOrg("Organization");
        userDto.setAbout("I don't like bats.");

        //Act
        Set<ConstraintViolation<CreateOrUpdateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidCreateUserDto_null() {
        CreateOrUpdateUserDto userDto = new CreateOrUpdateUserDto();

        //Act
        Set<ConstraintViolation<CreateOrUpdateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());

        //number of fields with not null/empty/blank
        assertEquals(3, violations.size());
    }

    @Test
    public void testInvalidCreateUserDto_blankName() {
        CreateOrUpdateUserDto userDto = new CreateOrUpdateUserDto();

        userDto.setUsername("");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<CreateOrUpdateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Create user DTO field 'username' cannot be blank") | violation.getMessage().equals("Username must be between 1 and 32 characters.")));
    }

    @Test
    public void testInvalidCreateUserDto_blankPassword() {
        CreateOrUpdateUserDto userDto = new CreateOrUpdateUserDto();

        userDto.setUsername("Batman");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<CreateOrUpdateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Create user DTO password cannot be blank") | violation.getMessage().equals("Users password must be between 8 and 32 characters")));
    }

    @Test
    public void testInvalidCreateUserDto_blankEmail() {
        CreateOrUpdateUserDto userDto = new CreateOrUpdateUserDto();

        userDto.setUsername("Batman");
        userDto.setEmail("");

        //Act
        Set<ConstraintViolation<CreateOrUpdateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().equals("Create user DTO field 'email' cannot be blank")));
    }

    @Test
    public void testValidCreateUserDto_validEmail() {
        CreateOrUpdateUserDto userDto = new CreateOrUpdateUserDto();

        userDto.setUsername("Batman");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<CreateOrUpdateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidCreateUserDto_invalidEmail() {
        CreateOrUpdateUserDto userDto = new CreateOrUpdateUserDto();

        userDto.setUsername("Batman");
        userDto.setEmail("batman.com");

        //Act
        Set<ConstraintViolation<CreateOrUpdateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidCreateUserDto_invalidUserNameMax() {
        CreateOrUpdateUserDto userDto = new CreateOrUpdateUserDto();

        userDto.setUsername("BatmanBigAndStrongAndToughAndBetterThanSpiderMan");
        userDto.setEmail("batman@gmail.com");

        //Act
        Set<ConstraintViolation<CreateOrUpdateUserDto>> violations = validator.validate(userDto);

        //Assert
        assertFalse(violations.isEmpty());
    }
}
