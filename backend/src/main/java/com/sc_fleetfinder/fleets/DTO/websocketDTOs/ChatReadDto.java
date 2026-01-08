package com.sc_fleetfinder.fleets.DTO.websocketDTOs;

public record ChatReadDto(
        Long conversationId,
        Long lastReadMsgId
) {}
