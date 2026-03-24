package com.sc_fleetfinder.fleets.scheduledTasks;

import com.sc_fleetfinder.fleets.config.ActivityTracking.UserActivityCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActivityCacheClean {

    private final UserActivityCache activityCache;

    @Scheduled(fixedDelayString = "PT5M")
    @Transactional
    public void cleanNotRecentlyActive() {
        int threshold = 2000;

        activityCache.scheduledClean(threshold);
    }
}
