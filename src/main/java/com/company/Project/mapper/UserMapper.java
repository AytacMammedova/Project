package com.company.Project.mapper;

import com.company.Project.model.dto.UserDto;
import com.company.Project.model.dto.request.UserAddDto;
import com.company.Project.model.dto.request.UserUpdateRequest;
import com.company.Project.model.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel ="spring")
public interface UserMapper {
    UserDto toUserDto(User user);
    List<UserDto> toUserDtoList(List<User>users);
    User toUser(UserAddDto userAddDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUser(UserUpdateRequest userUpdateRequest, @MappingTarget User user);
}
