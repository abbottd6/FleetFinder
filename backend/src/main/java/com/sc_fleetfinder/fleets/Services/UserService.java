package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(User user);
    User getUserById(Long id);
}
