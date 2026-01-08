package com.sc_fleetfinder.fleets.DTO.websocketDTOs;

import java.util.List;

public record ReceiveReadNotesDto(
        List<Long> readIds
) { }
