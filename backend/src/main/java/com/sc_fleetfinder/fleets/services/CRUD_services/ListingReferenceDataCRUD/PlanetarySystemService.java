package com.sc_fleetfinder.fleets.services.CRUD_services.ListingReferenceDataCRUD;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.PlanetarySystemDto;

import java.util.List;

public interface PlanetarySystemService {

    List<PlanetarySystemDto> getAllPlanetarySystems();
    PlanetarySystemDto getPlanetarySystemById(Integer id);
}
