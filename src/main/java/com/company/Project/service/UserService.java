package com.company.Project.service;

import com.company.Project.model.dto.AddressDto;
import com.company.Project.model.dto.UserDto;
import com.company.Project.model.dto.request.AddressAddDto;
import com.company.Project.model.dto.request.UserAddDto;
import com.company.Project.model.dto.request.UserUpdateRequest;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public interface UserService {
    //Related to user
    List<UserDto> getAllUsers();
    UserDto getById(Long id);
    UserDto add(UserAddDto userAddDto);
    UserDto update(Long id,UserUpdateRequest userUpdateRequest);
    void delete(Long id);

    //Related to User Address Relations
    List<AddressDto> getUserAddresses(Long userId);
    UserDto addAddressToUser(Long userId,AddressAddDto addressAddDto);
    UserDto updateUserAddress(Long userId,Long addressId,AddressAddDto addressAddDto);
    void deleteUserAddress(Long userId,Long addressId);


}
