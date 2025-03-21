package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateUserDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.UserResponseDto;
import com.sc_fleetfinder.fleets.entities.User;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;

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
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        if(users.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for all Users");
        }

        return users.stream()
               .map(this::convertToDto)
               .collect(Collectors.toList());
    }

    @Override
    @Validated
    public UserResponseDto createUser(@Valid CreateUserDto createUserDto) {
        Objects.requireNonNull(createUserDto, "userDto cannot be null");
            User user = convertToEntity(createUserDto);
        userRepository.save(user);
        return convertToDto(user);
    }

    @Override
    @Validated
    public UserResponseDto updateUser(Long id, @Valid UpdateUserDto updateUserDto) {
        User user = userRepository.findById(updateUserDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + updateUserDto.getUserId() + " not found"));

        BeanUtils.copyProperties(updateUserDto, user, "id");
        userRepository.save(user);

        return convertToDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found")));
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return convertToDto(user.get());
        }
        else {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
    }

    //this needs to be moved to a conversion service in v2 and configured to not pass sensitive info to the front end or
    //api endpoints
    public UserResponseDto convertToDto(User user) {

        //Entity 'User' contains a set of groupListing entities that also need to be converted to the response dto
        Set<GroupListingResponseDto> groupListingResponseDtos = user.getGroupListings().stream()
                        .map(groupListing -> modelMapper.map(groupListing, GroupListingResponseDto.class))
                        .collect(Collectors.toSet());

        UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);

        //Setting the converted groupListingDtos from above as the userResponseDto's set of group listings
        userResponseDto.setGroupListingsDto(groupListingResponseDtos);

        return userResponseDto;
    }

    public User convertToEntity(CreateUserDto createUserDto) {
        return modelMapper.map(createUserDto, User.class);
    }
}
