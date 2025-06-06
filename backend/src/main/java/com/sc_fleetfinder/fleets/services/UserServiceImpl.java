package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateOrUpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.InvalidUserDataException;
import com.sc_fleetfinder.fleets.exceptions.UserConflictException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
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

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final Validator beanValidator;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, Validator beanValidator) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.beanValidator = beanValidator;
    }

    @Override
    public List<PrivateUserResponseDto> getAllUsers() {
        List<Users> users = userRepository.findAll();

        if(users.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for all Users");
        }

        return users.stream()
               .map(this::convertToDto)
               .collect(Collectors.toList());
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
        userRepository.findByKeycloakId(keycloakId).ifPresent(existing -> {
            throw new UserConflictException(
                    "A user with Keycloak ID '" + keycloakId + "' already exists."
            );
        });

        // normalize email to lowercase and trim
        String normalizedEmail = rawEmail.trim().toLowerCase(Locale.ROOT);

        // check for email uniqueness
        userRepository.findByEmail(normalizedEmail).ifPresent(existing -> {
            throw new UserConflictException(
                    "A user with Email '" + normalizedEmail + "' already exists."
            );
        });

        // check username uniqueness
        userRepository.findByUsernameIgnoreCase(rawUsername).ifPresent(existing -> {
            throw new UserConflictException(
                    "A user with Username '" + rawUsername + "' already exists."
            );
        });

        // if uniqueness validators pass, create a dto to do bean validation on attributes
        CreateOrUpdateUserDto newUserDto = new CreateOrUpdateUserDto();
        newUserDto.setKeycloakId(keycloakId);
        newUserDto.setUsername(rawUsername);
        newUserDto.setEmail(rawEmail);

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

        return convertToDto(newUser);
    }

    @Override
    @Validated
    public PrivateUserResponseDto updateUser(Long id, @Valid UpdateUserDto updateUserDto) {
        Users users = userRepository.findById(updateUserDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Users with id " + updateUserDto.getUserId() + " not found"));

        BeanUtils.copyProperties(updateUserDto, users, "id");
        userRepository.save(users);

        return convertToDto(users);
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
            return convertToDto(user.get());
        }
        else {
            throw new ResourceNotFoundException("Users with id " + id + " not found");
        }
    }

    //this needs to be moved to a conversion service in v2 and configured to not pass sensitive info to the front end or
    //api endpoints
    public PrivateUserResponseDto convertToDto(Users users) {

        //Entity 'Users' contains a set of groupListing entities that also need to be converted to the response dto
        Set<GroupListingResponseDto> groupListingResponseDtos = users.getGroupListings().stream()
                        .map(groupListing -> modelMapper.map(groupListing, GroupListingResponseDto.class))
                        .collect(Collectors.toSet());

        PrivateUserResponseDto privateUserResponseDto = modelMapper.map(users, PrivateUserResponseDto.class);

        //Setting the converted groupListingDtos from above as the privateUserResponseDto's set of group listings
        privateUserResponseDto.setGroupListingsDto(groupListingResponseDtos);

        return privateUserResponseDto;
    }
}
