package com.sc_fleetfinder.fleets.entitiesTests;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void testUserGroupListingsPopulating() {
        Long userId = 1L;
        User fetchedUser = userRepository.findById(userId).orElse(null);

        assertNotNull(fetchedUser, "User should not be null");
        assertNotNull(fetchedUser.getGroupListings(), "GroupListings should not be null");
        assertFalse(fetchedUser.getGroupListings().isEmpty(), "GroupListings should not be empty");

        fetchedUser.getGroupListings().forEach(sub -> {
            assertEquals(userId, sub.getUser().getUserId(), "ListingUser should have the same ID as user");
        });

    }
}
