package com.sc_fleetfinder.fleets.DTO;

import com.sc_fleetfinder.fleets.entities.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupListingDTO {

    private int groupId;
    private String userName;
    private String server;
    private String environment;
    private String experience;
    private String listingTitle;
    private String listingUser;
    private String playStyle;
    private String legality;
    private String groupStatus;
    private LocalDateTime eventSchedule;
    private String category;
    private String subcategory;
    private String pvpStatus;
    private String system;
    private String planetMoonSystem;
    private String listingDescription;
    private int desiredPartySize;
    private String availableRoles;
    private String commsOption;
    private String commsService;
    private LocalDateTime creationTimestamp;
    private LocalDateTime lastUpdated;
}
