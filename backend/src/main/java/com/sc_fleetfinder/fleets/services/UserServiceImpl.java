package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        super();
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PrivateUserResponseDto> getAllUsers() {
        List<Users> users = userRepository.findAll();

        if(users.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for all Users");
        }

        return users.stream()
               .map(this::convertToDto)
               .collect(Collectors.toList());
    }

    @Override
    @Validated
    public PrivateUserResponseDto createUser(@Valid CreateUserDto createUserDto) {
        Objects.requireNonNull(createUserDto, "userDto cannot be null");
            Users users = convertToEntity(createUserDto);
        userRepository.save(users);
        return convertToDto(users);
    }

    @Override
    @Validated
    public PrivateUserResponseDto updateUser(Long id, @Valid UpdateUserDto updateUserDto) {
        Users users = userRepository.findById(updateUserDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Users with id " + updateUserDto.getUserId() + " not found"));

        BeanUtils.copyProperties(updateUserDto, users, "id");
        userRepository.save(users);

        return convertToDto(users);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Users with id " + id + " not found")));
    }

    @Override
    public PrivateUserResponseDto getUserById(Long id) {
        Optional<Users> user = userRepository.findById(id);
        if (user.isPresent()) {
            return convertToDto(user.get());
        }
        else {
            throw new ResourceNotFoundException("Users with id " + id + " not found");
        }
    }

    //this needs to be moved to a conversion service in v2 and configured to not pass sensitive info to the front end or
    //api endpoints
    public PrivateUserResponseDto convertToDto(Users users) {

        //Entity 'Users' contains a set of groupListing entities that also need to be converted to the response dto
        Set<GroupListingResponseDto> groupListingResponseDtos = users.getGroupListings().stream()
                        .map(groupListing -> modelMapper.map(groupListing, GroupListingResponseDto.class))
                        .collect(Collectors.toSet());

        PrivateUserResponseDto privateUserResponseDto = modelMapper.map(users, PrivateUserResponseDto.class);

        //Setting the converted groupListingDtos from above as the privateUserResponseDto's set of group listings
        privateUserResponseDto.setGroupListingsDto(groupListingResponseDtos);

        return privateUserResponseDto;
    }

    public Users convertToEntity(CreateUserDto createUserDto) {
        return modelMapper.map(createUserDto, Users.class);
    }
}
