package com.sc_fleetfinder.fleets.DTO.websocketDTOs;

public record UserUnreadTotalDto(
        Long userId,
        Long unreadCount
) {
}
