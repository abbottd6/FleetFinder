package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateOrUpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.InvalidUserDataException;
import com.sc_fleetfinder.fleets.exceptions.UserConflictException;
import com.sc_fleetfinder.fleets.services.conversion_services.UserConversionServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserConversionServiceImpl userConversionService;
    private final Validator beanValidator;

    public UserServiceImpl(UserRepository userRepository, UserConversionServiceImpl userConversionService, Validator beanValidator) {
        this.userRepository = userRepository;
        this.userConversionService = userConversionService;
        this.beanValidator = beanValidator;
    }

    @Override
    public List<PrivateUserResponseDto> getAllUsers() {
        List<Users> users = userRepository.findAll();

        if(users.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for all Users");
        }

        return users.stream()
               .map(userConversionService::convertToDto)
               .collect(Collectors.toList());
    }

    //helper method for normalizing emails. isolated for testing.
    String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 1) Perform repository-level uniqueness checks on keycloakId, email, and username.
     * 2) If no conflicts exist, build a CreateOrUpdateUserDto and run BeanValidation on it.
     * 3) If DTO is valid, create and save new Users, otherwise throw UserConflictException.
     */
    @Override
    @Validated
    @Transactional
    public PrivateUserResponseDto createUser(String keycloakId, String rawUsername, String rawEmail ) {
        // check for keycloakId uniqueness
        // new keycloakIds should always be unique, regardless of reused usernames/emails for deleted accounts,
        // so it doesnt matter if this isDeleted() or not
        userRepository.findByKeycloakId(keycloakId).ifPresent(existing -> {
            log.error("User Creation failed due to pre-existing Keycloak ID: {}", existing.getKeycloakId());
            throw new UserConflictException(
                    "A user with this ID already exists."
            );
        });

        // normalize email to lowercase and trim
        String normalizedEmail = normalizeEmail(rawEmail);

        // check for email uniqueness
        userRepository.findByEmail(normalizedEmail).ifPresent(existing -> {
            if(!existing.getIsDeleted()) {
                log.error("User creation requested for existing email: {}", existing.getEmail());
                throw new UserConflictException(
                        "A user with this email already exists."
                );
            }
        });

        // check username uniqueness
        userRepository.findByUsernameIgnoreCase(rawUsername).ifPresent(existing -> {
            if(!existing.getIsDeleted()) {
                log.error("User creation requested for existing username: {}", existing.getUsername());
                throw new UserConflictException(
                        "A user with Username '" + rawUsername + "' already exists."
                );
            }
        });

        // if uniqueness validators pass, create a dto to do bean validation on attributes
        CreateOrUpdateUserDto newUserDto = new CreateOrUpdateUserDto();
        newUserDto.setKeycloakId(keycloakId);
        newUserDto.setUsername(rawUsername);
        newUserDto.setEmail(normalizedEmail);

        // call bean validator on the dto
        Set<ConstraintViolation<CreateOrUpdateUserDto>> violations = beanValidator.validate(newUserDto);

        // prepare response if bean validation fails
        if (!violations.isEmpty()) {
            String combinedViolations = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new InvalidUserDataException(combinedViolations);
        }

        // map the validated new user to an entity
        Users newUser = new Users();
        newUser.setKeycloakId(keycloakId);
        newUser.setUsername(rawUsername);
        newUser.setEmail(rawEmail);
        newUser.setIsDeleted(false);

        userRepository.save(newUser);

        return userConversionService.convertToDto(newUser);
    }

    @Override
    @Validated
    public PrivateUserResponseDto updateUser(Long id, @Valid UpdateUserDto updateUserDto) {
        Users users = userRepository.findById(updateUserDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Users with id " + updateUserDto.getUserId() + " not found"));

        BeanUtils.copyProperties(updateUserDto, users, "id");
        userRepository.save(users);

        return userConversionService.convertToDto(users);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Users with id " + id + " not found")));
    }

    @Override
    public PrivateUserResponseDto getUserById(Long id) {
        Optional<Users> user = userRepository.findById(id);
        if (user.isPresent()) {
            return userConversionService.convertToDto(user.get());
        }
        else {
            throw new ResourceNotFoundException("Users with id " + id + " not found");
        }
    }
}
