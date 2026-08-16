package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.RsvpMasterRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.RsvpMasterResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.WrapperDtoRsvpActiveMastersResponse;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingService;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RsvpServiceImpl implements RsvpService {

    private final GroupListingService gls;
    private final InGroupRankService rankService;
    private final RsvpMasterRepository rsvpMasterRepo;
    private final ModelMapper modelMapper;

    @Override
    public WrapperDtoRsvpActiveMastersResponse getRsvpActiveMastersList(Users manager, Long listingId) {
        GroupListing listing = gls.findGroupListingEntityById(listingId);

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_ROSTERS);

        List<RsvpMasterResponseDto> masterDtoList = rsvpMasterRepo.findAllByListingId(listingId).stream()
                .map(p -> modelMapper.map(p, RsvpMasterResponseDto.class))
                .toList();

        WrapperDtoRsvpActiveMastersResponse response = new WrapperDtoRsvpActiveMastersResponse();

        response.setHasGlobalMaster(masterDtoList.stream()
                .anyMatch(r -> r.getSubgroupId() == null));

        response.setMastersList(masterDtoList);

        return response;
    }
}
