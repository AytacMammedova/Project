package com.company.Project.service.impl;

import com.company.Project.client.TransactionServiceClient;
import com.company.Project.exceptions.AddressOwnershipException;
import com.company.Project.exceptions.PasswordValidationException;
import com.company.Project.exceptions.UserNotFoundException;
import com.company.Project.mapper.AddressMapper;
import com.company.Project.mapper.UserMapper;
import com.company.Project.model.dto.AddressDto;
import com.company.Project.model.dto.UserDto;
import com.company.Project.model.dto.request.*;
import com.company.Project.model.dto.response.CustomerResponse;
import com.company.Project.model.entity.Address;
import com.company.Project.model.entity.User;
import com.company.Project.repository.AddressRepository;
import com.company.Project.repository.RoleRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.AddressService;
import com.company.Project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final TransactionServiceClient transactionServiceClient; // ADD THIS

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
        User savedUser = userRepository.save(user);

        // Transaction servisində customer yaratmaq
        try {
            CustomerCreateRequest customerRequest = CustomerCreateRequest.builder()
                    .name(savedUser.getName())
                    .email(savedUser.getEmail())
                    .phone(savedUser.getPhone())
                    .dateOfBirth(savedUser.getDateOfBirth())
                    .ecommerceUserId(savedUser.getId())
                    .build();

            CustomerResponse customerResponse = transactionServiceClient.createCustomer(customerRequest);
            log.info("Customer created in transaction service: {}", customerResponse.getCustomerId());

        } catch (Exception e) {
            log.info("Failed to create customer in transaction service: {}", e.getMessage());
            // Bu xəta critical deyil, davam edə bilərik
        }

        return userMapper.toUserDto(savedUser);
    }
//    @Override
//    public UserDto add(UserAddDto userAddDto) {
//        log.info("Creating new user: {}", userAddDto.getName());
//        User user=userMapper.toUser(userAddDto);
//        user.setRole(roleRepository.findByName("USER"));
//        User createdUser=userRepository.save(user);
//        log.info("Created user with ID: {} and name: {}", createdUser.getId(), createdUser.getName());
//        return userMapper.toUserDto(createdUser);
//    }

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
    @Override
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        // emailini aliriq
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new PasswordValidationException("Current password is incorrect");
        }

        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new PasswordValidationException("New password and confirmation do not match");
        }

        if (passwordEncoder.matches(passwordChangeDto.getNewPassword(), user.getPassword())) {
            throw new PasswordValidationException("New password must be different from current password");
        }
        
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", userEmail);
    }
    @Override
    public UserDto getByEmail(String email) {
        log.info("Getting user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user with email: " + email));
        return userMapper.toUserDto(user);
    }



}
