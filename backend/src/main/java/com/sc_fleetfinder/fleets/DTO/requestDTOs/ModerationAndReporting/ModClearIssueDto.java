package com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting;

import lombok.Data;

@Data
public class ModClearIssueDto {
    private Long issueId;
    private String note;
}
