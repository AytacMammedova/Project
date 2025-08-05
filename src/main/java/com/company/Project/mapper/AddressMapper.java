package com.company.Project.mapper;

import com.company.Project.model.dto.request.AddressAddDto;
import com.company.Project.model.dto.AddressDto;
import com.company.Project.model.entity.Address;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(componentModel ="spring")
public interface AddressMapper {
    AddressDto toAddressDto(Address address);
    List<AddressDto> toAddressDtoList(List<Address> addressList);
    List<Address>toAddressList(List<AddressAddDto>addressAddDtoList);
    Address toAddress(AddressAddDto addressAddDto);
    Address toAddressFromAddressDto(AddressDto addressDto);
}
