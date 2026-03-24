package com.sc_fleetfinder.fleets.config.ActivityTracking;

import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActivityCache {

    private final ConcurrentHashMap<String, Instant> lastAccess = new ConcurrentHashMap<>();
    private final UserService userService;

    public void recordAccess(String kcId) {
        Instant ts = lastAccess.put(kcId, Instant.now());

        Users user = userService.verifyUser(kcId);

        log.info("Updated: {}'s last access: {}.", kcId, ts );
    }

    public boolean hasRecentAccess(String kcId, int secondsThreshold) {
        Instant ts = lastAccess.get(kcId);
        return ts != null && ts.isAfter(Instant.now().minusSeconds(secondsThreshold));
    }

    public void scheduledClean(int secondsThreshold) {
        HashMap<String, Instant> toRemove = new HashMap<>();

        Instant threshold = Instant.now().minusSeconds(secondsThreshold);

        lastAccess.entrySet().stream()
                .filter(entry -> entry.getValue().isBefore(threshold))
                .forEach(entry -> {
                    Instant ts = lastAccess.remove(entry.getKey());
                    if (ts != null) {
                        toRemove.put(entry.getKey(), ts);
                    }
                });

        if(!toRemove.isEmpty()) {
            userService.updateLastActive(toRemove);
        }

        toRemove.forEach((key, value) -> log.info("Removed user: {} with ts: {}", key, value));
        lastAccess.forEach((key, value) -> log.info("Removed user: {} with ts: {}", key, value));

        toRemove.clear();
    }
}
