package com.sc_fleetfinder.fleets.DTO.websocketDTOs;

import java.util.Set;

public record UserUnreadResponseDto(
        Long totalUnread,
        Set<ConvUnreadMap> unreadByConv
) {
}
