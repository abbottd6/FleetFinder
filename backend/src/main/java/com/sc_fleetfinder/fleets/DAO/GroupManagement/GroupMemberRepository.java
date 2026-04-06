package com.sc_fleetfinder.fleets.DAO.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    Page<GroupMember> findAllByUser(Users user, Pageable pageable);
}
