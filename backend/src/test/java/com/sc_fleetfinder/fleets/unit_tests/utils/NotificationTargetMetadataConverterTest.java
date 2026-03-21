package com.sc_fleetfinder.fleets.unit_tests.utils;

import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadata;
import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadataConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class NotificationTargetMetadataConverterTest {

    private final NotificationTargetMetadataConverter converter = new NotificationTargetMetadataConverter();

    // ─── convertToDatabaseColumn ──────────────────────────────────────────────

    @Test
    void convertToDatabaseColumn_Null_ReturnsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToDatabaseColumn_ValidObject_ReturnsJsonString() {
        NotificationTargetMetadata metadata = new NotificationTargetMetadata(1L, "GroupListing", "My Fleet");

        String result = converter.convertToDatabaseColumn(metadata);

        assertThat(result).isNotNull();
        assertThat(result).contains("\"targetId\":1");
        assertThat(result).contains("\"targetType\":\"GroupListing\"");
        assertThat(result).contains("\"targetName\":\"My Fleet\"");
    }

    // ─── convertToEntityAttribute ─────────────────────────────────────────────

    @Test
    void convertToEntityAttribute_Null_ReturnsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_ValidJson_ReturnsCorrectObject() {
        String json = "{\"targetId\":1,\"targetType\":\"GroupListing\",\"targetName\":\"My Fleet\"}";

        NotificationTargetMetadata result = converter.convertToEntityAttribute(json);

        assertThat(result).isNotNull();
        assertThat(result.getTargetId()).isEqualTo(1L);
        assertThat(result.getTargetType()).isEqualTo("GroupListing");
        assertThat(result.getTargetName()).isEqualTo("My Fleet");
    }

    @Test
    void convertToEntityAttribute_MalformedJson_ThrowsRuntimeException() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("not-valid-json"))
                .isInstanceOf(RuntimeException.class);
    }
}
