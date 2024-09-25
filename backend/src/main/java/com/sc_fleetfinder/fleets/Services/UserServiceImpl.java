package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.GroupListingDto;
import com.sc_fleetfinder.fleets.DTO.UserDto;
import com.sc_fleetfinder.fleets.entities.User;
import com.sc_fleetfinder.fleets.Services.GroupListingServiceImpl;
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
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
               .map(this::convertToDto)
               .collect(Collectors.toList());
    }


    @Override
    public User createUser(@Valid UserDto userDto) {
        Objects.requireNonNull(userDto, "userDto cannot be null");
            User user = convertToEntity(userDto);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, @Valid UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        BeanUtils.copyProperties(userDto, user, "id");

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found")));
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return convertToDto(user.get());
        }
        else {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
    }

    public UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();

        Set<GroupListingDto> groupListingDtos = user.getGroupListings().stream()
                        .map(groupListing -> modelMapper.map(groupListing, GroupListingDto.class))
                        .collect(Collectors.toSet());
        userDto.setGroupListingsDto(groupListingDtos);
        modelMapper.map(user, userDto, "GroupListingDto");

        return userDto;
    }

    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
