package com.company.Project.mapper;

import com.company.Project.model.dto.SubTypeDto;
import com.company.Project.model.entity.SubType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel ="spring")

public interface SubTypeMapper {
    SubTypeDto toSubTypeDto(SubType subType);
    List<SubTypeDto> toSubTypeDtoList(List<SubType> subTypeList);
}
