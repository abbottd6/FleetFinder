package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
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
public class UserRequestDto {
    @NotNull(message = "User request DTO UserId cannot be null")
    private Long userId;
    @NotBlank(message = "User request DTO Username cannot be blank")
    @Size(min = 1, max = 32, message = "User request DTO Username must be between 1 and 32 characters")
    private String username;
    @NotBlank(message = "User request DTO password cannot be blank")
    @Size(min = 8, max = 32, message = "User request DTO User password must be between 8 and 32 characters in length")
    private String password;
    @NotBlank(message = "User request DTO User email cannot be blank")
    @Email
    private String email;
    private String server;
    private String org;
    private String about;
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDateTime acctCreated;
    private Set<GroupListingResponseDto> groupListingsDto = new HashSet<>();
}
