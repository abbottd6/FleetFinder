package com.sc_fleetfinder.fleets.unit_tests.config.mappers;

import com.sc_fleetfinder.fleets.config.mappers.ModelMapperConfig;
import com.sc_fleetfinder.fleets.services.MapperLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;

class GroupListingResponseDtoMapperConfigTest {

    private ModelMapper modelMapper;
    private MapperLookupService mls;

    @BeforeEach
    void setUp() {
        ModelMapperConfig mapperConfig = new ModelMapperConfig(mls);
        modelMapper = mapperConfig.fleetFinderModelMapper();
    }
}