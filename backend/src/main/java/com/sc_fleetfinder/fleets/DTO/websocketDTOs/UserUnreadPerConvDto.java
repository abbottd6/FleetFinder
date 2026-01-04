package com.sc_fleetfinder.fleets.DTO.websocketDTOs;

import java.util.Set;

public record UserUnreadPerConvDto(
        Long userId,
        Set<ConvUnreadMap> convIdAndUnread
) {
}
