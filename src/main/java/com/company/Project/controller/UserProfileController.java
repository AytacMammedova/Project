package com.company.Project.controller;

import com.company.Project.model.dto.AddressDto;
import com.company.Project.model.dto.UserDto;
import com.company.Project.model.dto.request.AddressAddDto;
import com.company.Project.model.dto.request.PasswordChangeDto;
import com.company.Project.model.dto.request.UserUpdateRequest;
import com.company.Project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    /**
     * Get current user's profile
     */
    @GetMapping
    public UserDto getMyProfile() {
        String userEmail = getCurrentUserEmail();
        return userService.getByEmail(userEmail); // You'll need to add this method
    }

    /**
     * Update current user's profile
     */
    @PutMapping
    public UserDto updateMyProfile(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        String userEmail = getCurrentUserEmail();
        Long userId = userService.getByEmail(userEmail).getId(); // You'll need getId in UserDto
        return userService.update(userId, userUpdateRequest);
    }

    /**
     * Get current user's addresses
     */
    @GetMapping("/addresses")
    public List<AddressDto> getMyAddresses() {
        String userEmail = getCurrentUserEmail();
        Long userId = userService.getByEmail(userEmail).getId();
        return userService.getUserAddresses(userId);
    }

    /**
     * Add address to current user
     */
    @PostMapping("/addresses")
    public UserDto addMyAddress(@Valid @RequestBody AddressAddDto addressAddDto) {
        String userEmail = getCurrentUserEmail();
        Long userId = userService.getByEmail(userEmail).getId();
        return userService.addAddressToUser(userId, addressAddDto);
    }

    /**
     * Update current user's address
     */
    @PutMapping("/addresses/{addressId}")
    public UserDto updateMyAddress(@PathVariable Long addressId,
                                   @Valid @RequestBody AddressAddDto addressAddDto) {
        String userEmail = getCurrentUserEmail();
        Long userId = userService.getByEmail(userEmail).getId();
        return userService.updateUserAddress(userId, addressId, addressAddDto);
    }

    /**
     * Delete current user's address
     */
    @DeleteMapping("/addresses/{addressId}")
    public void deleteMyAddress(@PathVariable Long addressId) {
        String userEmail = getCurrentUserEmail();
        Long userId = userService.getByEmail(userEmail).getId();
        userService.deleteUserAddress(userId, addressId);
    }

    /**
     * Change current user's password
     */
    @PutMapping("/change-password")
    public String changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        userService.changePassword(passwordChangeDto);
        return "Password changed successfully. Please login again with your new password.";
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}