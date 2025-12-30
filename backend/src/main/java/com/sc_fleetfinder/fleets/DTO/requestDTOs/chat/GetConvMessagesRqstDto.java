package com.sc_fleetfinder.fleets.DTO.requestDTOs.chat;

import lombok.Data;

@Data
public class GetConvMessagesRqstDto {

    private Long conversationId;
    private int pageIdx;
    private int pageSize;
}
