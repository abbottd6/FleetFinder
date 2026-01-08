package com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CommsOption {
    REQUIRED(1, "Required"),
    OPTIONAL(2, "Optional"),
    NO_COMMS(3, "No Comms");

    private final Integer id;
    private final String dbOption;

    CommsOption(Integer id, String dbOption) {
        this.id = id;
        this.dbOption = dbOption;
    }

    public static String getById(Integer id) {
        return Arrays.stream(values())
                .filter(e -> e.id.equals(id))
                .findFirst()
                .map(CommsOption::getDbOption)
                .orElseThrow(() -> new IllegalArgumentException("No such comms option: " + id));
    }
}

