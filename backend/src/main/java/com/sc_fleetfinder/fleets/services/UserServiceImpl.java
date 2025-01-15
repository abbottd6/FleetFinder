package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UserRequestDto;
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
        return users.stream()
               .map(this::convertToDto)
               .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto createUser(@Valid UserRequestDto userRequestDto) {
        Objects.requireNonNull(userRequestDto, "userDto cannot be null");
            User user = convertToEntity(userRequestDto);
        userRepository.save(user);
        return convertToDto(user);
    }

    @Override
    public UserResponseDto updateUser(@PathVariable Long id, @Valid UserRequestDto userRequestDto) {
        User user = userRepository.findById(userRequestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userRequestDto.getUserId() + " not found"));

        BeanUtils.copyProperties(userRequestDto, user, "id");
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

    public UserResponseDto convertToDto(User user) {
        Set<GroupListingResponseDto> groupListingResponseDtos = user.getGroupListings().stream()
                        .map(groupListing -> modelMapper.map(groupListing, GroupListingResponseDto.class))
                        .collect(Collectors.toSet());

        UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);

        userResponseDto.setGroupListingsDto(groupListingResponseDtos);

        return userResponseDto;
    }

    public User convertToEntity(UserRequestDto userRequestDto) {
        return modelMapper.map(userRequestDto, User.class);
    }
}
