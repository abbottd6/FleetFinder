package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDto {

    @NotNull(message = "Update user DTO field 'userId' cannot be null")
    private Long userId;

    @NotBlank(message = "Update user DTO field 'username' cannot be null")
    @Size(min = 1, max = 32, message = "Update user DTO field 'username' must be " +
            "between 1 and 32 characters")
    private String username;

    @NotBlank(message = "Update user DTO field 'email' cannot be null")
    @Email(message = "Update user DTO field 'email' must be a valid email address")
    private String email;

    private Integer serverId;

    @Size(max = 25, message = "Update user DTO field 'org' cannot exceed 25 characters")
    private String org;

    @Size(max = 255, message = "Create user DTO field 'about' cannot exceed 255 characters")
    private String about;
}
