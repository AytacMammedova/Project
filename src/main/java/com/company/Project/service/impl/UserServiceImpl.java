package com.company.Project.service.impl;

import com.company.Project.exceptions.UserNotFoundException;
import com.company.Project.mapper.AddressMapper;
import com.company.Project.mapper.UserMapper;
import com.company.Project.model.dto.UserDto;
import com.company.Project.model.dto.request.UserAddDto;
import com.company.Project.model.dto.request.UserUpdateRequest;
import com.company.Project.model.entity.User;
import com.company.Project.repository.RoleRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final AddressMapper addressMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    @Override
    public UserDto getById(Long id) {
        return userMapper.toUserDto(userRepository.findById(id).orElseThrow(IllegalStateException ::new));
    }

    @Override
    public UserDto add(UserAddDto userAddDto) {
        User user=userMapper.toUser(userAddDto);
        user.setRole(roleRepository.findByName("USER"));
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long id, UserUpdateRequest userUpdateRequest) {
        User user=userRepository.findById(id).orElseThrow(()->new UserNotFoundException("No user with id: "+id));
        User updatedUser = userMapper.updateUser(userUpdateRequest,user);
        userRepository.save(updatedUser);
        return userMapper.toUserDto(updatedUser);

    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }


}
