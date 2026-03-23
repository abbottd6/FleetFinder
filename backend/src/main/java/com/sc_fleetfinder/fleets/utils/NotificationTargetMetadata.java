package com.sc_fleetfinder.fleets.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTargetMetadata {

    private String noteTopic;

    @NotNull(message = "NotificationTargetMetadata class field 'targetId' cannot be null.")
    private Long targetId;

    private String targetLabel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS", timezone = "UTC")
    private Instant targetCreatedAt;

    private String addContext;
}

