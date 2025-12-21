package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class ModerationIssueResponseDto {

    @NotNull(message="ModerationIssueResponseDto field 'issueId' cannot be null.")
    private Long issueId;

    @NotNull(message="ModerationIssueResponseDto field 'groupId' cannot be null.")
    private Long groupId;

    @NotNull(message="ModerationIssueResponseDto field 'username' cannot be null.")
    private String username;

    @NotNull(message="ModerationIssueResponseDto field 'userId' cannot be null.")
    private Long userId;

    @NotNull(message="ModerationIssueResponseDto field 'reportTotalCount' cannot be null.")
    private Integer reportTotalCount;

    @NotNull(message="ModerationIssueResponseDto field 'spamCount' cannot be null.")
    private Integer spamCount;

    @NotNull(message="ModerationIssueResponseDto field 'hateSpeechCount' cannot be null.")
    private Integer hateSpeechCount;

    @NotNull(message="ModerationIssueResponseDto field 'nsfwCount' cannot be null.")
    private Integer nsfwCount;

    @NotNull(message="ModerationIssueResponseDto field 'scamCount' cannot be null.")
    private Integer scamCount;

    @NotNull(message="ModerationIssueResponseDto field 'offTopicCount' cannot be null.")
    private Integer offTopicCount;

    @NotNull(message="ModerationIssueResponseDto field 'trollCount' cannot be null.")
    private Integer trollCount;

    @NotNull(message="ModerationIssueResponseDto field 'doxxCount' cannot be null.")
    private Integer doxxCount;

    @NotNull(message="ModerationIssueResponseDto field 'cheatCount' cannot be null.")
    private Integer cheatCount;

    @NotNull(message="ModerationIssueResponseDto field 'otherCount' cannot be null.")
    private Integer otherCount;

    @NotNull(message="ModerationIssueResponseDto field 'firstReportTs' cannot be null.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant firstReportTs;

    @NotNull(message="ModerationIssueResponseDto field 'lastReportTs' cannot be null.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastReportTs;

    @NotNull(message="ModerationIssueResponseDto field 'status' cannot be null.")
    private String status;
}
