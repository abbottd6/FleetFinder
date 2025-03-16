package com.sc_fleetfinder.fleets.unit_tests.config.mappers;

import com.sc_fleetfinder.fleets.config.mappers.GroupListingResponseDtoMapperConfig;
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