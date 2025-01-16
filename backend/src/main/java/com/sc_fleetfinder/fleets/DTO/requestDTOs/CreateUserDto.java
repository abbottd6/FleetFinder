package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDto {

    @NotBlank(message = "Create user DTO field 'username' cannot be blank")
    @Size(min = 1, max = 32, message = "Create user DTO field 'username' must be " +
            "between 1 and 32 characters")
    private String username;

    @NotBlank(message = "Create user DTO password cannot be blank")
    @Size(min = 8, max = 32, message = "Create user DTO field 'password' " +
            "must be between 8 and 32 characters in length")
    private String password;

    @NotBlank(message = "Create user DTO field 'email' cannot be blank")
    @Email(message = "Create user field 'email' must be a valid email address")
    private String email;

    private String server;

    @Size(max = 25, message = "Create user DTO field 'org' cannot exceed 25 characters")
    private String org;

    @Size(max = 255, message = "Create user DTO field 'about' cannot exceed 255 characters")
    private String about;
}
