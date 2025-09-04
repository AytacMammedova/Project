package com.company.Project.controller;

import com.company.Project.model.dto.AddressDto;
import com.company.Project.model.dto.UserDto;
import com.company.Project.model.dto.request.AddressAddDto;
import com.company.Project.model.dto.request.PasswordChangeDto;
import com.company.Project.model.dto.request.UserAddDto;
import com.company.Project.model.dto.request.UserUpdateRequest;
import com.company.Project.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private UserDto userDto1;
    private UserDto userDto2;
    private UserAddDto userAddDto;
    private UserUpdateRequest userUpdateRequest;
    private AddressDto addressDto;
    private AddressAddDto addressAddDto;
    private PasswordChangeDto passwordChangeDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setName("John Doe");
        userDto1.setEmail("john@example.com");
        userDto1.setPhone("+994501234567");
        userDto1.setDateOfBirth(LocalDate.of(1990, 1, 1));

        userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("Jane Smith");
        userDto2.setEmail("jane@example.com");
        userDto2.setPhone("+994507654321");
        userDto2.setDateOfBirth(LocalDate.of(1995, 5, 15));

        userAddDto = new UserAddDto();
        userAddDto.setName("New User");
        userAddDto.setEmail("newuser@example.com");
        userAddDto.setPhone("+994509876543");
        userAddDto.setDateOfBirth(LocalDate.of(1992, 3, 10));

        userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setName("Updated Name");
        userUpdateRequest.setEmail("updated@example.com");
        userUpdateRequest.setPhone("+994501111111");

        addressDto = new AddressDto();
        addressDto.setCity("Baku");
        addressDto.setRegion("Nasimi");
        addressDto.setStreet("Nizami street 123");
        addressDto.setAddressDesc("Near metro station");

        addressAddDto = new AddressAddDto();
        addressAddDto.setCity("Baku");
        addressAddDto.setRegion("Nasimi");
        addressAddDto.setStreet("Nizami street 123");
        addressAddDto.setAddressDesc("Near metro station");

        passwordChangeDto = new PasswordChangeDto();
        passwordChangeDto.setCurrentPassword("oldPassword123");
        passwordChangeDto.setNewPassword("newPassword123");
        passwordChangeDto.setConfirmPassword("newPassword123");
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        // Given
        List<UserDto> users = Arrays.asList(userDto1, userDto2);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")))
                .andExpect(jsonPath("$[1].email", is("jane@example.com")));

        verify(userService).getAllUsers();
    }

    @Test
    void getById_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        Long userId = 1L;
        when(userService.getById(userId)).thenReturn(userDto1);

        // When & Then
        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.phone", is("+994501234567")));

        verify(userService).getById(userId);
    }

    @Test
    void add_WithValidData_ShouldCreateUser() throws Exception {
        // Given
        when(userService.add(any(UserAddDto.class))).thenReturn(userDto1);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAddDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService).add(any(UserAddDto.class));
    }

    @Test
    void add_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        UserAddDto invalidUser = new UserAddDto();
        invalidUser.setName(""); // Invalid - empty name
        invalidUser.setEmail("invalid-email"); // Invalid email format

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(any(UserAddDto.class));
    }

    @Test
    void update_WithValidData_ShouldUpdateUser() throws Exception {
        // Given
        Long userId = 1L;
        UserDto updatedUser = new UserDto();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");

        when(userService.update(eq(userId), any(UserUpdateRequest.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService).update(eq(userId), any(UserUpdateRequest.class));
    }

    @Test
    void delete_ShouldDeleteUser() throws Exception {
        // Given
        Long userId = 1L;
        doNothing().when(userService).delete(userId);

        // When & Then
        mockMvc.perform(delete("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }

    @Test
    void getUserAddresses_ShouldReturnAddressList() throws Exception {
        // Given
        Long userId = 1L;
        List<AddressDto> addresses = Arrays.asList(addressDto);
        when(userService.getUserAddresses(userId)).thenReturn(addresses);

        // When & Then
        mockMvc.perform(get("/users/{userId}/addresses", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].city", is("Baku")))
                .andExpect(jsonPath("$[0].region", is("Nasimi")));

        verify(userService).getUserAddresses(userId);
    }

    @Test
    void addAddressToUser_WithValidData_ShouldAddAddress() throws Exception {
        // Given
        Long userId = 1L;
        when(userService.addAddressToUser(eq(userId), any(AddressAddDto.class))).thenReturn(userDto1);

        // When & Then
        mockMvc.perform(post("/users/{userId}/addresses", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressAddDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("John Doe")));

        verify(userService).addAddressToUser(eq(userId), any(AddressAddDto.class));
    }

    @Test
    void updateUserAddress_ShouldUpdateAddress() throws Exception {
        // Given
        Long userId = 1L;
        Long addressId = 1L;
        when(userService.updateUserAddress(eq(userId), eq(addressId), any(AddressAddDto.class))).thenReturn(userDto1);

        // When & Then
        mockMvc.perform(put("/users/{userId}/address/{addressId}", userId, addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressAddDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("John Doe")));

        verify(userService).updateUserAddress(eq(userId), eq(addressId), any(AddressAddDto.class));
    }

    @Test
    void deleteUserAddress_ShouldDeleteAddress() throws Exception {
        // Given
        Long userId = 1L;
        Long addressId = 1L;
        doNothing().when(userService).deleteUserAddress(userId, addressId);

        // When & Then
        mockMvc.perform(delete("/users/{userId}/addresses/{addressId}", userId, addressId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).deleteUserAddress(userId, addressId);
    }

    @Test
    void changePassword_WithValidData_ShouldChangePassword() throws Exception {
        // Given
        doNothing().when(userService).changePassword(any(PasswordChangeDto.class));

        // When & Then
        mockMvc.perform(put("/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Password changed successfully")));

        verify(userService).changePassword(any(PasswordChangeDto.class));
    }

    @Test
    void changePassword_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        PasswordChangeDto invalidDto = new PasswordChangeDto();
        invalidDto.setCurrentPassword(""); // Invalid - empty current password
        invalidDto.setNewPassword("123"); // Invalid - too short
        invalidDto.setConfirmPassword("456"); // Invalid - doesn't match

        // When & Then
        mockMvc.perform(put("/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).changePassword(any(PasswordChangeDto.class));
    }
}