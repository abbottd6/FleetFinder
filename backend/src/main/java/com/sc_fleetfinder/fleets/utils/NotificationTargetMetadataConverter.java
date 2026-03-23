package com.sc_fleetfinder.fleets.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NotificationTargetMetadataConverter implements AttributeConverter<NotificationTargetMetadata, String> {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(NotificationTargetMetadata notificationTargetMetadata) {
        if (notificationTargetMetadata == null) return null;

        try {
            return objectMapper.writeValueAsString(notificationTargetMetadata);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting NotificationTargetMetadata to JSON", e);
        }
    }

    @Override
    public NotificationTargetMetadata convertToEntityAttribute(String dbData) {
        if(dbData == null) return null;
        try {
            return objectMapper.readValue(dbData, NotificationTargetMetadata.class);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting database JSON to NotificationTargetMetadata.class", e);
        }
    }
}
