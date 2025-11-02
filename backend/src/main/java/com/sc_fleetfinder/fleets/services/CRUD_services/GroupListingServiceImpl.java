package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.DeleteGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupListingConversionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class GroupListingServiceImpl implements GroupListingService {

    private static final Logger log = LoggerFactory.getLogger(GroupListingServiceImpl.class);
    private final GroupListingRepository groupListingRepository;
    private final GroupListingConversionService groupListingConversionService;

    @PersistenceContext
    private EntityManager em;

    public GroupListingServiceImpl(GroupListingRepository groupListingRepository,
                                   GroupListingConversionService groupListingConversionService) {

        this.groupListingRepository = groupListingRepository;
        this.groupListingConversionService = groupListingConversionService;
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
    @Validated
    @Transactional(transactionManager = "transactionManager")
    public ResponseEntity<?> createGroupListing(@Valid CreateGroupListingDto createGroupListingDto) {
        Objects.requireNonNull(createGroupListingDto, "GroupListingResponseDto cannot be null");
            try {
                GroupListing groupListing = groupListingConversionService.convertToEntity(createGroupListingDto);

                groupListingRepository.save(groupListing);

                Map<String, String> response = new HashMap<>();
                response.put("listingTitle", groupListing.getListingTitle());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            catch (Exception e) {
                log.error("CreateGroupListing failed. Reason: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred while creating your listing.");
            }
    }

    @Override
    @Validated
    @Transactional(transactionManager = "transactionManager")
    public GroupListing updateGroupListing(Long id, @Valid UpdateGroupListingDto updateGroupListingDto) {
        GroupListing groupListing = groupListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));


        //ADD LOGIC TO CHECK THE NUMBER OF GROUPLISTINGS ASSOCIATED WITH A USER.
        //LIMIT THE NUMBER OF GROUP LISTINGS PER USER TO 3

        BeanUtils.copyProperties(updateGroupListingDto, groupListing, "groupId", "user", "listingUser");

        return groupListingRepository.save(groupListing);
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public ResponseEntity<?> deleteGroupListing(DeleteGroupListingDto deleteDto) {
            try {
                GroupListing groupEntity = groupListingRepository.findById(deleteDto.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException(deleteDto.getGroupId()));

                if (Objects.equals(deleteDto.getUserId(), groupEntity.getUsers().getUserId())) {
                    log.info("request id: {}", deleteDto.getUserId());
                    log.info("entity id: {}", groupEntity.getUsers().getUserId());
                    groupEntity.setDeleted(true);
                    groupEntity.setDeletedAt(Instant.now());
                    groupListingRepository.flush();
                    groupListingRepository.save(groupEntity);

                    log.info("DELETED");
                }
                else {
                    log.error("Delete DTO and listing repository userIds do not match. \n Request userId: {} \n " +
                            "Repository userId: {}", deleteDto.getUserId(), groupEntity.getUsers().getUserId());
                }

                log.info("EM class: {}", em.getClass().getName());
                log.info("Tx active? {}",
                        org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive());

                Map<String, String> response = new HashMap<>();
                response.put("listingTitle", groupEntity.getListingTitle());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            catch (Exception e) {
                log.error("DeleteGroupListing failed. Reason: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred while deleting this listing. The action could not be completed.");
            }
    }

    @Override
    public GroupListingResponseDto getGroupListingById(Long id) {
        GroupListing groupListing = groupListingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("GetGroupListingById failed to find an entity with the given group Id: {}.", id);
                    return new ResourceNotFoundException(id);
                });

        return groupListingConversionService.convertListingToResponseDto(groupListing);
    }
}
