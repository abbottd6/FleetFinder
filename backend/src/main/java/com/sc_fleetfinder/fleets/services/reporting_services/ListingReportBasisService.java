package com.sc_fleetfinder.fleets.services.reporting_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReportBasisDto;

import java.util.List;

public interface ListingReportBasisService {

    List<ListingReportBasisDto> getAll();
}
