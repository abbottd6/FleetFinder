package com.sc_fleetfinder.fleets.DTO;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDTO {

    private Long userId;
    private String username;
    private String password;
    private String email;
    private int serverId;
    private String org;
    private String about;
    private LocalDateTime acctCreated;
    private Set<GroupListing> groupListings;
}
