package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PublicUserResponseDto {

    @NotNull(message = "PublicUserResponseDto 'UserId' cannot be null")
    private Long userId;

    @NotBlank(message = "PublicUserResponseDto 'username' cannot be blank, empty, or null")
    @Size(min = 1, max = 32, message = "PublicUserResponseDto 'username' must be " +
            "between 1 and 32 characters")
    private String username;

    private String server;

    @Size(max = 25, message = "PublicUserResponseDto 'org' cannot exceed 25 characters")
    private String org;

    @Size(max = 255, message = "PublicUserResponseDto 'about' cannot exceed 255 characters")
    private String about;

    public PublicUserResponseDto() {}
}
