package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportRepository;
import com.sc_fleetfinder.fleets.DAO.NewListingNotifyQueueRepository;
import com.sc_fleetfinder.fleets.DAO.NotificationOutboxRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SearchListingsDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.CommsOption;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.NewListingNotifyQueue;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.GroupManagement.GroupMemberManagementService;
import com.sc_fleetfinder.fleets.services.archive_services.ArchiveService;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupListingConversionService;
import com.sc_fleetfinder.fleets.utils.SearchStopWords;
import com.sc_fleetfinder.fleets.utils.VisStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationConstants.USER_MAX_LISTING_COUNT;

@Service
@Validated
@RequiredArgsConstructor
public class GroupListingServiceImpl implements GroupListingService {

    private static final Logger log = LoggerFactory.getLogger(GroupListingServiceImpl.class);
    private final GroupListingRepository groupListingRepository;
    private final GroupListingConversionService groupListingConversionService;
    private final UserRepository userRepository;
    private final ArchiveService archiveService;
    private final HiddenListingService hls;
    private final ListingReportRepository lrr;
    private final NotificationOutboxRepository outboxRepo;
    private final NewListingNotifyQueueRepository listingNotifyQueueRepository;
    private final GroupMemberManagementService memberManagementService;

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<GroupListingResponseDto> getAllGroupListings() {
        List<GroupListing> groupListings = groupListingRepository.findAll();

        if(groupListings.isEmpty()) {
            log.info("No group listings found.");
        }
        return groupListings.stream()
                .map(groupListingConversionService::convertListingToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GroupListingResponseDto> searchGroupListings(SearchListingsDto dto, Pageable pageable,
                                                             Optional<Users> userOpt) {
        Specification<GroupListing> spec = buildListingFilterSpec(dto);

        if(userOpt.isPresent()) {
            Users user = userOpt.get();
            spec = spec.and(notHiddenBy(user)).and(notReportedBy(user));
        }

        Page<GroupListing> groupListings = groupListingRepository.findAll(spec, pageable);
        return groupListings.map(groupListingConversionService::convertListingToResponseDto);
    }

    @Override
    @Validated
    @Transactional
    public ResponseEntity<?> createGroupListing(@Valid CreateGroupListingDto dto, Users requestingUser) {
        Objects.requireNonNull(dto, "GroupListingResponseDto cannot be null");
        Objects.requireNonNull(requestingUser, "User cannot be null");

        if(requestingUser.getGroupListings().size() >= USER_MAX_LISTING_COUNT) {
            Map<String, String> response = new HashMap<>();
            response.put("response", "Listing creation unsuccesful. Limit of " + USER_MAX_LISTING_COUNT + " reached.");

            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

        } else {
            try {
                GroupListing groupListing = groupListingConversionService.convertToEntity(dto);

                groupListing.setUsers(requestingUser);
                groupListing.setLastUpdated(Instant.now());

                GroupListing listingWithId = groupListingRepository.save(groupListing);
                groupListingRepository.flush();

                log.info("Listing ID: {}", listingWithId.getGroupId());

                memberManagementService.createOwnerMember(requestingUser, listingWithId);

                NewListingNotifyQueue queued = new NewListingNotifyQueue(listingWithId);

                listingNotifyQueueRepository.save(queued);

                Map<String, String> response = new HashMap<>();
                response.put("listingTitle", groupListing.getListingTitle());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } catch (Exception e) {
                log.error("CreateGroupListing failed. Reason: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred while creating your listing.");
            }
        }
    }

    @Override
    @Validated
    @Transactional
    public ResponseEntity<?> updateGroupListing(@Valid UpdateGroupListingDto dto, Users user) {
        try {
            GroupListing listing = groupListingRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException(dto.getGroupId()));

            if (Objects.equals(listing.getUsers().getUserId(), user.getUserId())) {
                GroupListing temp = groupListingConversionService.convertToEntity(dto);

                BeanUtils.copyProperties(temp, listing,
                        "groupId", "users", "creationTimestamp", "isDeleted", "deletedAt");

                listing.setLastUpdated(Instant.now());
                listing.setVisStatus(VisStatus.FRESH);

                groupListingRepository.save(listing);

                int obDeletedCount = outboxRepo.deleteOutboxNotificationsOnEntityUpdate(user.getUserId(),
                        listing.getGroupId(), "GROUP_LISTING");

                log.info("User update triggered deletion of {} outbox notifications", obDeletedCount);

                Map<String, String> response = new HashMap<>();
                String title = listing.getListingTitle();
                response.put("listingTitle", title.length() <= 25 ? title : title.substring(0, 25) + "...");

                return ResponseEntity.status(HttpStatus.OK).body(response);

            } else {
                throw new ActionNotAuthorizedException(user.getUserId(), "update", "GroupListing", dto.getGroupId());
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ActionNotAuthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteGroupListing(Long groupId, Users user) {
            try {
                GroupListing listing = groupListingRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("GroupListing", groupId));

                if (Objects.equals(listing.getUsers().getUserId(), user.getUserId())) {

                    ListingArchive transientArchive = archiveService.prepareUserDeleteRecords(listing, user);

                    //probably move all the deletes to the event handler
                    user.getGroupListings().remove(listing);
                    userRepository.save(user);

                    groupListingRepository.delete(listing);
                    groupListingRepository.flush();

                    archiveService.archiveListing(transientArchive);

                    Map<String, String> response = new HashMap<>();
                    response.put("listingId", String.valueOf(listing.getGroupId()));
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
                else {
                    log.error(
                            """
                            Delete DTO and listing repository userIds do not match.
                                Request userId: {}
                                Listing entity: {}
                                Entity belongs to userId: {}
                            """,
                            user.getUserId(), listing.getGroupId(), listing.getUsers().getUserId()
                    );
                    throw new ActionNotAuthorizedException(
                            user.getUserId(), "delete", "GroupListing", listing.getGroupId()
                    );
                }
            }
            catch (ResourceNotFoundException e) {
                log.error("DeleteGroupListing failed. Reason: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(e.getMessage());
            }
            catch (ActionNotAuthorizedException e) {
                log.error("DeleteGroupListing failed. Reason: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            }
            catch (Exception e) {
                log.error("DeleteGroupListing failed. Reason: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred while deleting your listing: " + e.getMessage());
            }
    }

    @Override
    public GroupListingResponseDto getGroupListingDtoById(Long id) {
        GroupListing groupListing = findGroupListingEntityById(id);

        return groupListingConversionService.convertListingToResponseDto(groupListing);
    }

    @Override
    public GroupListing findGroupListingEntityById(Long groupId) {
        return groupListingRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("GetGroupListingById failed to find an entity with the given group Id: {}.", groupId);
                    return new ResourceNotFoundException("GroupListing", groupId);
                });
    }

    private Specification<GroupListing> notHiddenBy(Users user) {
        return (root, query, cb) -> {
            Set<Long> hiddenGroupIds = hls.getMyHiddenListingsBrief(user);
            if (hiddenGroupIds.isEmpty()) {
                return cb.conjunction();
            }
            return cb.not(root.get("groupId").in(hiddenGroupIds));
        };
    }

    private Specification<GroupListing> notReportedBy(Users user) {
        return (root, query, cb) -> {
            Set<Long> reportedGroupIds = lrr.findByReportingUserRef(user);
            if (reportedGroupIds.isEmpty()) {
                return cb.conjunction();
            }
            return cb.not(root.get("groupId").in(reportedGroupIds));
        };
    }

    private Specification<GroupListing> buildListingFilterSpec(SearchListingsDto dto) {
        Specification<GroupListing> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and((root, query, cb) -> cb.notEqual(root.get("visStatus"), (VisStatus.EXPIRED)));

        String search = dto.getSearch();

        if (search != null && !search.isBlank()) {
            String[] searchArray = search.trim().split("\\s+");
            List<String> tokens = new ArrayList<>();

            for (String word : searchArray) {
                if (word.isBlank()) continue;

                String lower = word.toLowerCase(Locale.ROOT);

                String alphaOnly = lower.replaceAll("[^a-z]", "");

                boolean hasDigit = word.chars().anyMatch(Character::isDigit);

                boolean isStopWord = SearchStopWords.ENGLISH.contains(alphaOnly);

                if(!isStopWord || hasDigit) {
                    tokens.add(lower);
                }
            }

            if(!tokens.isEmpty()) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> tokenPredicates = new ArrayList<>();

                    for (String token : tokens) {
                        String like = "%" + token + "%";
                        Predicate perToken = criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("listingTitle")), like),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("listingDescription")), like),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("availableRoles")), like),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("commsService")), like)
                        );
                        tokenPredicates.add(perToken);
                    }

                    if (tokenPredicates.isEmpty()) {
                        return criteriaBuilder.conjunction();
                    }

                    return criteriaBuilder.or(tokenPredicates.toArray(new Predicate[0]));
                });
            }
        }

        for(Field field: SearchListingsDto.class.getDeclaredFields()) {
            String fieldName = field.getName();

            if(fieldName.equals("search") || fieldName.equals("page") || fieldName.equals("size")) {
                continue;
            }

            field.setAccessible(true);
            Object value;
            try {
                value = field.get(dto);
            } catch (IllegalAccessException e) {
                continue;
            }

            if(value == null) {
                continue;
            }

            if(fieldName.equals("commsOption")) {
                Integer lookupId = (Integer) value;
                String tempOption = CommsOption.getById(lookupId);

                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get(fieldName), tempOption));
            }

            else if(fieldName.equals("languageCode")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get(fieldName), value));
            }

            else if(fieldName.equals("dateStart")) {
                LocalDate date = (LocalDate) value;
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get("eventSchedule"), date)
                );
            }
            else if(fieldName.equals("dateEnd")) {
                LocalDate date = (LocalDate) value;
                //add one day to the filter date so that anything with a time on the last day of filter end is included
                LocalDate datePlus = date.plusDays(1);
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThan(root.get("eventSchedule"), datePlus)
                );
            }

            else if (value instanceof Integer lookupId) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get(fieldName).get("id"), lookupId));
            }
        }

        return spec;
    }
}
