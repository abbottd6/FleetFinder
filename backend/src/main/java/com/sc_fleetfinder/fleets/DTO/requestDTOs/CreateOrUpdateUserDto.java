package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Data
public class CreateOrUpdateUserDto {

    //##TODO should not include a keycloakid nor a userid, should be derived from token
    @NotNull(message= "Create User DTO keycloak_id cannot be null")
    @JdbcTypeCode(Types.VARCHAR)
    private String keycloakId;

    @NotBlank(message = "Create user DTO field 'username' cannot be blank")
    @Size(min = 3, max = 32, message = "Create user DTO field 'username' must be " +
            "between 3 and 32 characters")
    private String username;

    @NotBlank(message = "Create user DTO field 'email' cannot be blank")
    @Email(message = "Create user field 'email' must be a valid email address")
    private String email;

    @Size(max = 20, message = "CreateOrUpdateUserDto field 'discordId' cannot exceed 20 characters.")
    private String discordId;

    private String server;

    @Size(max = 25, message = "Create user DTO field 'org' cannot exceed 25 characters")
    private String org;

    @Size(max = 255, message = "Create user DTO field 'about' cannot exceed 255 characters")
    private String about;
}
