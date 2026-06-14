package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewRoleClassificationRepository;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewRoleClassification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrewRoleServiceImpl implements CrewRoleService {

    private final CrewRoleClassificationRepository crcr;

    @Override
    public List<CrewRoleClassification> getRolesForListingByUserId(Long userId) {
        return crcr.findByUserAndGlobalClassifications(userId);
    }
}
