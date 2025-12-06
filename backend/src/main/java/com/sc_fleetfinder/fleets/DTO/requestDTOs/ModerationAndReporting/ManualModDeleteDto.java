package com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ManualModDeleteDto {

    @NotNull(message="ManualModDeleteDto field 'groupId' cannot be null.")
    private Long groupId;

    @NotNull(message="ManualModDeleteDto field 'reportBasis' cannot be null.")
    private Integer reportBasis;

    @Nullable
    @Size(max = 255, message="ManualModDeleteDto field 'note' cannot be longer than 255 characters." )
    private String note;
}
