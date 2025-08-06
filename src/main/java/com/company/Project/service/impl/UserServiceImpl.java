package com.company.Project.service.impl;

import com.company.Project.exceptions.AddressOwnershipException;
import com.company.Project.exceptions.UserNotFoundException;
import com.company.Project.mapper.AddressMapper;
import com.company.Project.mapper.UserMapper;
import com.company.Project.model.dto.AddressDto;
import com.company.Project.model.dto.UserDto;
import com.company.Project.model.dto.request.AddressAddDto;
import com.company.Project.model.dto.request.UserAddDto;
import com.company.Project.model.dto.request.UserUpdateRequest;
import com.company.Project.model.entity.Address;
import com.company.Project.model.entity.User;
import com.company.Project.repository.AddressRepository;
import com.company.Project.repository.RoleRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.AddressService;
import com.company.Project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final AddressMapper addressMapper;
    private final AddressService addressService;
    private  final AddressRepository addressRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Getting all users...");
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    @Override
    public UserDto getById(Long id) {
        log.info("Retrieved user by ID: {}", id);
        return userMapper.toUserDto(userRepository.findById(id).orElseThrow(()->new UserNotFoundException("No user with id: "+id)));
    }

    @Override
    public UserDto add(UserAddDto userAddDto) {
        log.info("Creating new user: {}", userAddDto.getName());
        User user=userMapper.toUser(userAddDto);
        user.setRole(roleRepository.findByName("USER"));
        User createdUser=userRepository.save(user);
        log.info("Created user with ID: {} and name: {}", createdUser.getId(), createdUser.getName());
        return userMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto update(Long id, UserUpdateRequest userUpdateRequest) {
        User user=userRepository.findById(id).orElseThrow(()->new UserNotFoundException("No user with id: "+id));
        log.info("Updating user with ID: {}",id);
        userMapper.updateUser(userUpdateRequest, user);
        User updatedUser = userRepository.save(user);
        log.info("Successfully updated user with id: {}", id);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("No user with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("Deleted user with id: {}", id);
    }

    @Override
    public UserDto addAddressToUser(Long userId, AddressAddDto addressAddDto) {
        log.info("Adding address to user: {}", userId);

        User user=userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("No user with id: "+userId));

        user.getAddresses().add(addressMapper.toAddress(addressAddDto));
        userRepository.save(user);
        log.info("Successfully added address to user {}", userId);
        return userMapper.toUserDto(user);
    }



    @Override
    public UserDto updateUserAddress(Long userId, Long addressId, AddressAddDto addressAddDto) {
        log.info("Updating address {} for user {}", addressId, userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("No user with id: " + userId);
        }
        if(!addressRepository.isAddressOwnedByUser(addressId,userId)){
            throw new AddressOwnershipException("This address doesn't belong to user with id "+userId);
        }

        addressService.update(addressId, addressAddDto);
        User refreshedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found after update"));

        log.info("Successfully updated address {} for user {}", addressId, userId);
        return  userMapper.toUserDto(refreshedUser);

        }

    @Override
    public void deleteUserAddress(Long userId, Long addressId) {
        log.info("Deleting address {} for user {}", addressId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user with id: " + userId));

        if(!addressRepository.isAddressOwnedByUser(addressId,userId)){
            throw new AddressOwnershipException("This address doesn't belong to user with id "+userId);
        }

        if (user.getAddresses() != null) {
            user.getAddresses().removeIf(address -> address.getId().equals(addressId));
            userRepository.save(user);
        }

        log.info("Successfully deleted address {} for user {}", addressId, userId);

    }

    @Override
    public List<AddressDto> getUserAddresses(Long userId) {
        log.info("Getting addresses for user: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("No user with id: " + userId);
        }
        return addressService.getAddressesByUserId(userId);
    }


}
