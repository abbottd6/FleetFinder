package com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePushSubRequestDto {

    @NotBlank(message = "CreatePushSubRequestDto field 'userLabel' cannot be null or blank.")
    @Size(min=2, max=32, message = "CreatePushSubRequestDto field 'userLabel' must be between 2 and 32 characters.")
    private String userLabel;

    @NotBlank(message = "CreatePushSubRequestDto field 'deviceUrl' cannot be null or blank.")
    private String deviceUrl;

    @NotBlank(message = "CreatePushSubRequestDto field 'publicKey' cannot be null or blank.")
    private String publicKey;

    @NotBlank(message = "CreatePushSubRequestDto field 'browserSecret' cannot be null or blank.")
    private String browserSecret;
}
