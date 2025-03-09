package com.sc_fleetfinder.fleets.config.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;

class GroupListingResponseDtoMapperConfigTest {

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        GroupListingResponseDtoMapperConfig mapperConfig = new GroupListingResponseDtoMapperConfig();
        modelMapper = mapperConfig.groupListingResponseDtoMapper();
    }
}