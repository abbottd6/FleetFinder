package com.sc_fleetfinder.fleets.config.mappers.GroupManagementMappers;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupRankAssignedPrivilegeRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupManagerMemberResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupMembershipResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.entities.GroupManagement.RankPrivilegeType;
import com.sc_fleetfinder.fleets.services.GroupManagement.InGroupRankService;
import com.sc_fleetfinder.fleets.utils.GroupManagement.AssignedPrivilegeId;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class GroupMemberMapperConfig {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern(
            "MM/dd/yy HH:mm").withZone(ZoneOffset.UTC);

    private GroupRankAssignedPrivilegeRepository assignedPrivilegeRepository;

    @Bean("GroupMemberMapper")
    public ModelMapper groupMemberMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addConverter(new AbstractConverter<Instant, String>() {
            @Override
            protected String convert(Instant source) {
                return source != null ? UTC_FORMATTER.format(source) : null;
            }
        });

        modelMapper.createTypeMap(GroupMember.class, GroupMembershipResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(GroupMember::getUser, GroupMembershipResponseDto::setUserSummary);

                    mapper.map(GroupMember::getRosterClass, GroupMembershipResponseDto::setMemberStatus);

                    mapper.map(GroupMember::getMemberNote, GroupMembershipResponseDto::setMemberNote);

                    mapper.map(GroupMember::getHasComms, GroupMembershipResponseDto::setHasComms);

                    mapper.map(GroupMember::getHasExtNotes, GroupMembershipResponseDto::setHasExtNotes);

                    mapper.map(GroupMember::getRsvpStatus, GroupMembershipResponseDto::setRsvpStatus);

                    mapper.map(GroupMember::getCreatedAt, GroupMembershipResponseDto::setJoinedAt);

                    mapper.map(GroupMember::getMemberRank, GroupMembershipResponseDto::setMemberRank);

                    mapper.using(ctx -> {
                        InGroupRank rank = (InGroupRank) ctx.getSource();
                        return hasGroupManagementPrivileges(rank);
                    }).map(GroupMember::getMemberRank, GroupMembershipResponseDto::setIsAuthorizedManager);

                    mapper.map(GroupMember::getGroupListing, GroupMembershipResponseDto::setListing);
                });

        modelMapper.createTypeMap(GroupMember.class, GroupManagerMemberResponseDto.class)
                .addMappings(mapper -> {
                    mapper.using(ctx -> {
                        GroupListing listing = (GroupListing) ctx.getSource();
                        return listing != null ? listing.getGroupId() : null;
                    }).map(GroupMember::getGroupListing, GroupManagerMemberResponseDto::setListingId);

                    mapper.map(GroupMember::getUser, GroupManagerMemberResponseDto::setUserSummary);

                    mapper.map(GroupMember::getRosterClass, GroupManagerMemberResponseDto::setMemberStatus);

                    mapper.map(GroupMember::getMemberNote, GroupManagerMemberResponseDto::setMemberNote);

                    mapper.map(GroupMember::getHasComms, GroupManagerMemberResponseDto::setHasComms);

                    mapper.map(GroupMember::getHasExtNotes, GroupManagerMemberResponseDto::setHasExtNotes);

                    mapper.map(GroupMember::getRsvpStatus, GroupManagerMemberResponseDto::setRsvpStatus);

                    mapper.map(GroupMember::getCreatedAt, GroupManagerMemberResponseDto::setJoinedAt);

                    mapper.map(GroupMember::getMemberRank, GroupManagerMemberResponseDto::setMemberRank);
                });

        return modelMapper;
    }

    private boolean hasGroupManagementPrivileges(InGroupRank rank) {
        if(rank == null) return false;
        List<RankPrivilegeOptions> privileges = assignedPrivilegeRepository.getAssignedPrivilegesByRank(rank);

        if(privileges.isEmpty()) return false;

        Set<RankPrivilegeOptions> managementPrivileges = Set.of(
                RankPrivilegeOptions.MANAGE_RANKS,
                RankPrivilegeOptions.MANAGE_POSITIONS,
                RankPrivilegeOptions.MANAGE_ROLES,
                RankPrivilegeOptions.MANAGE_ROSTERS,
                RankPrivilegeOptions.MANAGE_SUBGROUPS
        );

        return privileges.stream().anyMatch(managementPrivileges::contains);
    }
}
