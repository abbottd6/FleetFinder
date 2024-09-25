package com.sc_fleetfinder.fleets.DTO;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sc_fleetfinder.fleets.entities.GroupListing;
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
public class UserDto {

    @NotNull(message = "UserId cannot be null")
    private Long userId;
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, max = 32, message = "Username must be between 1 and 32 characters")
    private String username;
    @NotBlank(message = "User password cannot be blank")
    @Size(min = 8, max = 32, message = "User password must be between 8 and 32 characters")
    private String password;
    @NotBlank(message = "User email cannot be blank")
    @Email
    private String email;
    private String server;
    private String org;
    private String about;
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDateTime acctCreated;
    private Set<GroupListingDto> groupListingsDto = new HashSet<>();
}
