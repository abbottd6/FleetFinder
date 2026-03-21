package com.sc_fleetfinder.fleets.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTargetMetadata {

    @NotNull(message = "NotificationTargetMetadata class field 'targetId' cannot be null.")
    private Long targetId;

    @NotNull(message = "NotificationTargetMetadata class field 'targetType' cannot be null.")
    private String targetType;

    private String targetName;
}

