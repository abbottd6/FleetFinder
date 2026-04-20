package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateUserDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.UpdateUserNotePrefDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.UserSummaryResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PublicUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.events.UserAccountDeleteEvent;
import com.sc_fleetfinder.fleets.events.UserRemoveDiscLinkEvent;
import com.sc_fleetfinder.fleets.exceptions.InvalidUserDataException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.exceptions.UserConflictException;
import com.sc_fleetfinder.fleets.projections.UserSearchProjection;
import com.sc_fleetfinder.fleets.services.CRUD_services.ListingReferenceDataCRUD.ServerRegionServiceImpl;
import com.sc_fleetfinder.fleets.services.Keycloak_Services.KeycloakAdminServiceImpl;
import com.sc_fleetfinder.fleets.services.conversion_services.UserConversionServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.persistence.criteria.Predicate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserConversionServiceImpl userConversionService;
    private final Validator beanValidator;
    private final GroupListingRepository groupListingRepository;
    private final KeycloakAdminServiceImpl kcAdminService;
    private final ApplicationEventPublisher eventPublisher;
    private final ServerRegionServiceImpl serverService;
    private final ModelMapper modelMapper;


    public UserServiceImpl(UserRepository userRepository, UserConversionServiceImpl userConversionService,
                           Validator beanValidator, GroupListingRepository groupListingRepository,
                           KeycloakAdminServiceImpl kcAdminService, ApplicationEventPublisher eventPublisher,
                           ServerRegionServiceImpl serverService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.userConversionService = userConversionService;
        this.beanValidator = beanValidator;
        this.groupListingRepository = groupListingRepository;
        this.kcAdminService = kcAdminService;
        this.eventPublisher = eventPublisher;
        this.serverService = serverService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PublicUserResponseDto> getAllUsers() {
        List<Users> users = userRepository.findAll();

        if(users.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for all Users");
        }

        return users.stream()
               .map(userConversionService::convertToPublicDto)
               .collect(Collectors.toList());
    }

    @Override
    public PublicUserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .filter(u -> !u.getIsDeleted())
                .map(userConversionService::convertToPublicDto)
                .orElseThrow(() -> {
                    log.info("Attempt to access public user data by userId failed due to nonexistent userId: " +
                            "{}", id);
                    return new ResourceNotFoundException("Users with id " + id + " not found");
                });
    }

    /**
     * 1) Perform repository-level uniqueness checks on keycloakId, email, and username.
     * 2) If no conflicts exist, build a CreateUserDto and run BeanValidation on it.
     * 3) If DTO is valid, create and save new Users, otherwise throw UserConflictException.
     */
    @Override
    @Validated
    @Transactional
    public PrivateUserResponseDto createUser(String keycloakId, String rawUsername, String rawEmail,
                                             String discordId, String discordUsername) {
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

        // check discordId uniqueness
        if(discordId != null) {
            userRepository.findByDiscordId(discordId).ifPresent(existing -> {
                if (!existing.getIsDeleted()) {
                    log.error("User creation requested for existing Discord ID: {}", existing.getDiscordId());
                    throw new UserConflictException(
                            "A user with Discord ID :'" + discordId + "' already exists."
                    );
                }
            });
        }

        // if uniqueness validators pass, create a dto to do bean validation on attributes
        CreateUserDto newUserDto = new CreateUserDto();
        newUserDto.setKeycloakId(keycloakId);
        newUserDto.setUsername(rawUsername);
        newUserDto.setEmail(normalizedEmail);
        newUserDto.setDiscordId(discordId);
        newUserDto.setDiscordUsername(discordUsername);

        // call bean validator on the dto
        Set<ConstraintViolation<CreateUserDto>> violations = beanValidator.validate(newUserDto);

        // prepare response if bean validation fails
        if (!violations.isEmpty()) {
            String combinedViolations = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new InvalidUserDataException(combinedViolations);
        }

        // map the validated new user to an entity
        Users newUser = new Users();
        newUser.setKeycloakId(newUserDto.getKeycloakId());
        newUser.setUsername(newUserDto.getUsername());
        newUser.setEmail(newUserDto.getEmail());
        newUser.setDiscordId(newUserDto.getDiscordId());
        newUser.setDiscordUsername(newUserDto.getDiscordUsername());
        newUser.setInGameUsername(newUserDto.getUsername());

        newUser.setIsDeleted(false);

        userRepository.save(newUser);

        return userConversionService.convertToPrivateDto(newUser);
    }

    @Override
    @Validated
    @Transactional
    public PrivateUserResponseDto updateUser(String kcId, @Valid UpdateUserDto updateUserDto) {
        Users user = userRepository.findByKeycloakId(kcId)
                .orElseThrow(() -> new ResourceNotFoundException("Users with id " + kcId + " not found"));

        try {
            if(updateUserDto.getServerId() != null) {
                user.setServer(serverService.getServerEntityById(updateUserDto.getServerId()));
            }
            user.setOrg(updateUserDto.getOrg());

            userRepository.save(user);
        }
        catch (Exception e) {
            throw new InvalidUserDataException(e.getMessage());
        }

        return userConversionService.convertToPrivateDto(user);
    }

    @Override
    @Transactional
    public Boolean updateUserNotificationPreference(Users user, UpdateUserNotePrefDto dto) {
        Boolean returnVal;

        switch (dto.getLabel()) {
            case "sysNotes":
                user.setExternalSysNotesEnabled(dto.getValue());
                userRepository.saveAndFlush(user);
                returnVal = user.getExternalSysNotesEnabled();
                break;
            case "groupNotes":
                user.setExternalGroupNotesEnabled(dto.getValue());
                userRepository.saveAndFlush(user);
                returnVal = user.getExternalGroupNotesEnabled();
                break;
            case "socialNotes":
                user.setExternalSocialNotesEnabled(dto.getValue());
                userRepository.saveAndFlush(user);
                returnVal = user.getExternalSocialNotesEnabled();
                break;
            default:
                throw new InvalidUserDataException(dto.getLabel() + " is not a valid label");
        }

        return returnVal;
    }

    @Override
    public String generateUserDiscordLink(String token) {

        return kcAdminService.generateDiscordKeycloakLink(token);
    }

    @Override
    @Transactional
    public PrivateUserResponseDto removeDiscordAccountLink(String kcId) {
        Users user = userRepository.findByKeycloakId(kcId)
                .orElseThrow(() -> new ResourceNotFoundException("Users with id " + kcId + " not found"));

        user.setDiscordId(null);
        user.setDiscordUsername(null);
        user.setExternalSysNotesEnabled(false);
        user.setExternalGroupNotesEnabled(false);
        user.setExternalSocialNotesEnabled(false);

        userRepository.save(user);
        userRepository.flush();

        eventPublisher.publishEvent(new UserRemoveDiscLinkEvent(user));

        return userConversionService.convertToPrivateDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(String kcId) {
        Users toDelete = userRepository.findByKeycloakId(kcId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot delete user when kcId " + kcId + " not found"));

        groupListingRepository.expireAllUserListingsOnDelete(toDelete.getUserId());

        toDelete.setEmail("deleted_" + toDelete.getUserId() + "@deleted.com");
        toDelete.setUsername("deleted_" + toDelete.getUserId());
        toDelete.setServer(null);
        toDelete.setOrg(null);
        toDelete.setAbout(null);
        toDelete.setDiscordId(null);
        toDelete.setDiscordUsername(null);
        toDelete.setExternalSysNotesEnabled(false);
        toDelete.setExternalGroupNotesEnabled(false);
        toDelete.setExternalSocialNotesEnabled(false);
        toDelete.setIsDeleted(true);
        toDelete.setKeycloakId("deleted_" + toDelete.getUserId());

        try {
            kcAdminService.deleteKeycloakUser(kcId);
        }
        catch(Exception e) {
            log.warn(e.getMessage());
        }

        userRepository.save(toDelete);

        eventPublisher.publishEvent(new UserAccountDeleteEvent(toDelete));

        log.info("Deleted user with id {}", toDelete.getUserId());
    }

    @Override
    public PrivateUserResponseDto getUserByKeycloakIdAndCheckDiscord(String kcId,
                                                                     String discId, String discName) {
        Users user = userRepository.findByKeycloakId(kcId).filter(u -> !u.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Users with id " + kcId + " not found"));

        if(discId != null && user.getDiscordId() == null) {
            user.setDiscordId(discId);
            user.setDiscordUsername(discName);
        }

        user.setLastAccess(Instant.now());
        userRepository.save(user);

        return userConversionService.convertToPrivateDto(user);
    }

    @Override
    public Optional<Users> getUserByKeycloakIdDoNotThrow(String kcId) {
        return userRepository.findByKeycloakId(kcId);
    }

    @Override
    public Users verifyUser(String kcId) {
            return userRepository.findByKeycloakId(kcId)
                    .filter(u -> !u.getIsDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("User with keycloakId "
                            + kcId + " not found"));
    }

    @Override
    @Transactional
    public void updateLastActive(HashMap<String, Instant> toUpdate) {
        toUpdate.forEach(userRepository::updateUserLastAccess);
    }

    @Override
    public Page<UserSummaryResponseDto> searchUsers(Users sessionUser, String searchCriteria) {

        Specification<Users> spec = buildUserSearchFilterSpec(sessionUser.getUserId(), searchCriteria);

        Pageable pageable = PageRequest.of(0, 50);

        Page<Users> found = userRepository.findAll(spec, pageable);
        return found.map(u -> modelMapper.map(u, UserSummaryResponseDto.class));
    }

    //helper method for normalizing emails. isolated for testing.
    String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private Specification<Users> buildUserSearchFilterSpec(Long sessionUserId, String search) {
        Specification<Users> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and((root, query, cb) -> cb.notEqual(root.get("isDeleted"), true));
        spec = spec.and((root, query, cb) -> cb.notEqual(root.get("userId"), sessionUserId));

        Long searchId = null;
        List<String> searchTerms = new ArrayList<>();

        if(search != null && !search.isBlank()) {
            String[] searchArray = search.trim().split("#");

            for(String term : searchArray) {
                if(term.isBlank()) continue;

                if(term.chars().allMatch(Character::isDigit)) {
                    searchId = Long.parseLong(term);
                } else {
                    searchTerms.add(term.toLowerCase(Locale.ROOT));
                }
            }

            log.info("SearchTerms: {}, size: {}", searchTerms.getFirst(), searchTerms.size());
            if(!searchTerms.isEmpty()) {
                log.info("does this run?");
                spec = spec.and((root, query, cb) -> {
                    List<Predicate> tokenPredicates = new ArrayList<>();

                    for(String token : searchTerms) {
                        String like = "%" + token + "%";
                        log.info("token: {}", like);
                        Predicate perToken = cb.or(
                                cb.like(cb.lower(root.get("username")), like),
                                cb.like(cb.lower(root.get("discordUsername")), like),
                                cb.like(cb.lower(root.get("inGameUsername")), like)
                        );
                        tokenPredicates.add(perToken);
                    }

                    if(tokenPredicates.isEmpty()) {
                        return cb.conjunction();
                    }

                    return cb.or(tokenPredicates.toArray(new Predicate[0]));
                });
            }

            if(searchId != null) {
                Long finalSearchId = searchId;
                spec = spec.or((root, query, cb) ->
                    cb.equal(root.get("userId"), finalSearchId));
            }
        }

        return spec;
    }
}
