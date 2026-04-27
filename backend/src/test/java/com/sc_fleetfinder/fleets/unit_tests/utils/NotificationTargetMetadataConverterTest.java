package com.sc_fleetfinder.fleets.unit_tests.utils;

import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadata;
import com.sc_fleetfinder.fleets.utils.NotificationTargetMetadataConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

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
        NotificationTargetMetadata metadata = new NotificationTargetMetadata(
                "Test topic", 32L, "My Fleet", "Test Status",
                Instant.parse("2026-03-23T08:06:31Z"), "Context Label",
                "Context status", null, "1");

        String result = converter.convertToDatabaseColumn(metadata);

        assertThat(result).isNotNull();
        assertThat(result).contains("\"targetId\":32");
        assertThat(result).contains("\"noteTopic\":\"Test topic\"");
        assertThat(result).contains("\"targetLabel\":\"My Fleet\"");
        assertThat(result).contains("\"addContext\":\"1\"");
    }

    @Test
    void convertToDatabaseColumn_WithTimestamp_SerializesInMySqlFormat() {
        NotificationTargetMetadata metadata = new NotificationTargetMetadata(
                null, 32L, "My Fleet", "Test Status", Instant.parse("2026-03-23T08:06:31Z"),
                "Context Label", "Context Status", Instant.parse("2026-03-23T08:06:31Z"), null);

        String result = converter.convertToDatabaseColumn(metadata);

        assertThat(result).contains("\"targetCreatedAt\":\"2026-03-23 08:06:31.000000\"");
    }

    // ─── convertToEntityAttribute ─────────────────────────────────────────────

    @Test
    void convertToEntityAttribute_Null_ReturnsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_ValidJson_ReturnsCorrectObject() {
        String json = "{\"noteTopic\":\"Test topic\",\"targetId\":32," +
                "\"targetLabel\":\"My Fleet\",\"addContext\":\"1\"}";

        NotificationTargetMetadata result = converter.convertToEntityAttribute(json);

        assertThat(result).isNotNull();
        assertThat(result.getTargetId()).isEqualTo(32L);
        assertThat(result.getNoteTopic()).isEqualTo("Test topic");
        assertThat(result.getTargetLabel()).isEqualTo("My Fleet");
        assertThat(result.getAddContext()).isEqualTo("1");
        assertThat(result.getTargetCreatedAt()).isNull();
    }

    @Test
    void convertToEntityAttribute_WithTimestamp_ParsesInstantCorrectly() {
        String json = "{\"targetId\":32,\"targetCreatedAt\":\"2026-03-23 08:06:31.000000\"}";

        NotificationTargetMetadata result = converter.convertToEntityAttribute(json);

        assertThat(result.getTargetCreatedAt()).isEqualTo(Instant.parse("2026-03-23T08:06:31Z"));
    }

    @Test
    void convertToEntityAttribute_MalformedJson_ThrowsRuntimeException() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("not-valid-json"))
                .isInstanceOf(RuntimeException.class);
    }
}
