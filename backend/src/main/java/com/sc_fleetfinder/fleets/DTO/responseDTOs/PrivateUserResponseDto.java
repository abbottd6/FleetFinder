package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class PrivateUserResponseDto {

    @NotNull(message = "UserId cannot be null")
    private Long userId;
    @NotNull(message = "user keycloak_id cannot be null")
    private String keycloakId;
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, max = 32, message = "Username must be between 1 and 32 characters")
    private String username;
    @NotBlank(message = "Users email cannot be blank")
    @Email
    private String email;
    private String server;
    private String org;
    private String about;
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDateTime acctCreated;
    private Set<GroupListingResponseDto> groupListingsDto = new HashSet<>();

    public PrivateUserResponseDto() {}

    public PrivateUserResponseDto(Long userId, String kcId, String username, String email) {
        this.userId = userId;
        this.keycloakId = kcId;
        this.username = username;
        this.email = email;
    }
}
