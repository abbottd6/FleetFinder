package com.sc_fleetfinder.fleets.DTO.requestDTOs.chat;

import lombok.Data;

@Data
public class GetConvMessagesRqstDto {

    private int pageIdx;
    private int pageSize;
    private Long conversationId;
}
