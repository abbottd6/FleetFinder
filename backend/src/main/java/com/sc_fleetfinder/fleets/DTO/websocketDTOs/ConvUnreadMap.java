package com.sc_fleetfinder.fleets.DTO.websocketDTOs;

public record ConvUnreadMap(
   Long conversationId,
   Long unreadCount
){ }
