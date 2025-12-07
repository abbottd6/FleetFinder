package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.SearchListingsDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.CommsOption;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.archive_services.ArchiveService;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupListingConversionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationConstants.USER_MAX_LISTING_COUNT;

@Service
@Validated
public class GroupListingServiceImpl implements GroupListingService {

    private static final Logger log = LoggerFactory.getLogger(GroupListingServiceImpl.class);
    private final GroupListingRepository groupListingRepository;
    private final GroupListingConversionService groupListingConversionService;
    private final UserRepository userRepository;
    private final ArchiveService archiveService;

    @PersistenceContext
    private EntityManager em;

    public GroupListingServiceImpl(GroupListingRepository groupListingRepository,
                                   GroupListingConversionService groupListingConversionService,
                                   UserRepository userRepository,
                                   ArchiveService archiveService) {

        this.groupListingRepository = groupListingRepository;
        this.groupListingConversionService = groupListingConversionService;
        this.userRepository = userRepository;
        this.archiveService = archiveService;
    }

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
    public Page<GroupListingResponseDto> searchGroupListings(SearchListingsDto dto, Pageable pageable) {
        Specification<GroupListing> spec = buildListingFilterSpec(dto);

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

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);

        } else {
            try {
                GroupListing groupListing = groupListingConversionService.convertToEntity(dto);

                groupListingRepository.save(groupListing);

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

                groupListingRepository.save(listing);

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

                if (Objects.equals(listing.getUsers(), user)) {

                    archiveService.prepareUserDeleteRecords(listing, user);

                    //probably move all the deletes to the event handler
                    user.getGroupListings().remove(listing);
                    userRepository.save(user);

                    groupListingRepository.delete(listing);
                    groupListingRepository.flush();

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
    public GroupListingResponseDto getGroupListingById(Long id) {
        GroupListing groupListing = groupListingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("GetGroupListingById failed to find an entity with the given group Id: {}.", id);
                    return new ResourceNotFoundException("GroupListing", id);
                });

        return groupListingConversionService.convertListingToResponseDto(groupListing);
    }

    private Map<String, Integer> parseSearchFilters(List<String> filters) {
        Map<String, Integer> result = new HashMap<>();
        if (filters == null || filters.isEmpty()) {
            return result;
        }

        /* THIS BREAKS ON COMMS OPTION FILTER BECAUSE THE OPTIONS DONT HAVE IDs

         */

        for (String filter : filters) {
            String[] parts = filter.split(":", 3);
            if (parts.length == 2) {
                String fieldName = parts[0].trim();
                String lookup = parts[1].substring(0, parts[1].indexOf('*'));
                Integer lookupId = Integer.valueOf(lookup);
                result.put(fieldName, lookupId);
            } else if (parts.length == 3) {
                String fieldName = parts[0].trim();
                String lookup = parts[1].substring(0, parts[1].indexOf('*'));
                Integer lookupId = Integer.valueOf(lookup);
                result.put(fieldName, lookupId);
                if(fieldName.equals("category")) {
                    String subfieldName = "subcategory";
                    String sublookup = parts[2].substring(0, parts[2].indexOf('*'));
                    Integer sublookupId = Integer.valueOf(sublookup);
                    result.put(subfieldName, sublookupId);
                }
                else if(fieldName.equals("system")) {
                    String subfieldName = "planetMoonSystem";
                    String sublookup = parts[2].substring(0, parts[2].indexOf('*'));
                    Integer sublookupId = Integer.valueOf(sublookup);
                    result.put(subfieldName, sublookupId);
                }
            }
        }
        return result;
    }

    //TODO move this into its own service package
    private Specification<GroupListing> buildListingFilterSpec(SearchListingsDto dto) {
        Specification<GroupListing> spec = Specification.where(null);

        String search = dto.getSearch();

        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";

            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("listingTitle")), like),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("listingDescription")), like),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("availableRoles")), like)
                    )
            );
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

            else if(fieldName.equals("dateStart")) {
                LocalDate date = (LocalDate) value;
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get("eventSchedule"), date)
                );
            }
            else if(fieldName.equals("dateEnd")) {
                LocalDate date = (LocalDate) value;
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThanOrEqualTo(root.get("eventSchedule"), date)
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
