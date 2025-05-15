package com.company.Project.service;

import com.company.Project.model.dto.UserDto;
import com.company.Project.model.dto.request.UserAddDto;
import com.company.Project.model.dto.request.UserUpdateRequest;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getById(Integer id);
    UserDto add(UserAddDto userAddDto);
    UserDto update(Integer id, UserUpdateRequest userUpdateRequest);
    void delete(Integer id);

}
