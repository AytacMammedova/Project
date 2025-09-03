package com.company.Project.service;

import com.company.Project.exceptions.UserNotFoundException;
import com.company.Project.mapper.UserMapper;
import com.company.Project.model.dto.UserDto;
import com.company.Project.model.dto.request.UserAddDto;
import com.company.Project.model.dto.request.UserUpdateRequest;
import com.company.Project.model.entity.Role;
import com.company.Project.model.entity.User;
import com.company.Project.repository.RoleRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private UserAddDto userAddDto;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1);
        userRole.setName("USER");
        userRole.setRoleDesc("Regular user");

        user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setPhone("+994501234567");
        user1.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user1.setRole(userRole);

        user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");
        user2.setPhone("+994507654321");
        user2.setDateOfBirth(LocalDate.of(2000, 5, 15));
        user2.setRole(userRole);


        userDto1 = new UserDto();
        userDto1.setName("John Doe");
        userDto1.setEmail("john@example.com");
        userDto1.setPhone("+994501234567");
        userDto1.setDateOfBirth(LocalDate.of(1990, 1, 1));

        userDto2 = new UserDto();
        userDto2.setName("Jane Smith");
        userDto2.setEmail("jane@example.com");
        userDto2.setPhone("+994507654321");
        userDto2.setDateOfBirth(LocalDate.of(2000, 5, 15));

        userAddDto = new UserAddDto();
        userAddDto.setName("New User");
        userAddDto.setEmail("newuser@example.com");
        userAddDto.setPhone("+994509876543");
        userAddDto.setDateOfBirth(LocalDate.of(1995, 3, 10));
    }

    @Test
    void getAllUsers_ShouldReturnUserDtoList() {

        List<User> users = Arrays.asList(user1, user2);
        List<UserDto> expectedUserDtos = Arrays.asList(userDto1, userDto2);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserDtoList(users)).thenReturn(expectedUserDtos);

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getName()).isEqualTo("Jane Smith");

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toUserDtoList(users);
    }

    @Test
    void getById_WhenUserExists_ShouldReturnUserDto() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        UserDto result = userService.getById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toUserDto(user1);
    }

    @Test
    void getById_WhenUserNotExists_ShouldThrowException() {
        Long userId = 10L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("No user with id: 10");

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).toUserDto(any());
    }

    @Test
    void add_ShouldCreateUserSuccessfully() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("newuser@example.com");
        newUser.setPhone("+994509876543");
        newUser.setDateOfBirth(LocalDate.of(1995, 3, 10));
        newUser.setRole(userRole);

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setName("New User");
        savedUser.setEmail("newuser@example.com");
        savedUser.setPhone("+994509876543");
        savedUser.setDateOfBirth(LocalDate.of(1995, 3, 10));
        savedUser.setRole(userRole);

        UserDto expectedDto = new UserDto();
        expectedDto.setName("New User");
        expectedDto.setEmail("newuser@example.com");
        expectedDto.setPhone("+994509876543");
        expectedDto.setDateOfBirth(LocalDate.of(1995, 3, 10));

        when(userMapper.toUser(userAddDto)).thenReturn(newUser);
        when(roleRepository.findByName("USER")).thenReturn(userRole);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserDto(savedUser)).thenReturn(expectedDto);

        UserDto result = userService.add(userAddDto);
        assertThat(result.getName()).isEqualTo("New User");
        assertThat(result.getEmail()).isEqualTo("newuser@example.com");

        verify(userMapper, times(1)).toUser(userAddDto);
        verify(roleRepository, times(1)).findByName("USER");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toUserDto(savedUser);
    }

    @Test
    void update_WhenUserExists_ShouldUpdateSuccessfully() {
        Long userId = 1L;
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updated@example.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPhone("+994501234567");
        updatedUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        updatedUser.setRole(userRole);

        UserDto expectedDto = new UserDto();
        expectedDto.setName("Updated Name");
        expectedDto.setEmail("updated@example.com");
        expectedDto.setPhone("+994501234567");
        expectedDto.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(userMapper.updateUser(updateRequest, user1)).thenReturn(user1);
        when(userRepository.save(user1)).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(expectedDto);

        UserDto result = userService.update(userId, updateRequest);
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).updateUser(updateRequest, user1);
        verify(userRepository, times(1)).save(user1);
        verify(userMapper, times(1)).toUserDto(updatedUser);
    }

    @Test
    void update_WhenUserNotExists_ShouldThrowException() {
        Long userId = 10L;
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("Updated Name");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userId, updateRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("No user with id: 10");

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_WhenUserExists_ShouldDeleteSuccessfully() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void delete_WhenUserNotExists_ShouldThrowException() {
        Long userId = 10L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("No user with id: 10");

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }
}