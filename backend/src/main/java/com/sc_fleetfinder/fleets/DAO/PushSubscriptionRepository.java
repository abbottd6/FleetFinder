package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.PushSubscription;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {

    Page<PushSubscription> getPageOfPushSubscriptionsByUser(Users user, Pageable pageable);

    Optional<PushSubscription> findByUserAndDeviceUrl(Users user, String deviceUrl);

    Optional<PushSubscription> findByUserAndIdPushSub(Users user, Long idPushSub);

    Integer deleteByUserAndIdPushSub(Users user, Long idPushSub);

    Set<PushSubscription> getSetOfPushSubscriptionsByUser(Users user);
}
