package com.sc_fleetfinder.fleets.unit_tests.scheduledTasks;

import com.sc_fleetfinder.fleets.config.ActivityTracking.UserActivityCache;
import com.sc_fleetfinder.fleets.scheduledTasks.UserActivityCacheClean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserActivityCacheCleanTest {

    @Mock
    private UserActivityCache activityCache;

    @InjectMocks
    private UserActivityCacheClean userActivityCacheClean;

    @Test
    void cleanNotRecentlyActive_CallsScheduledCleanWithThreshold300() {
        userActivityCacheClean.cleanNotRecentlyActive();

        verify(activityCache).scheduledClean(300);
    }
}
