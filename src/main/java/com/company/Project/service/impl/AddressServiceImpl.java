package com.company.Project.service.impl;

import com.company.Project.mapper.AddressMapper;
import com.company.Project.model.dto.request.AddressAddDto;
import com.company.Project.model.dto.AddressDto;
import com.company.Project.model.entity.Address;

import com.company.Project.repository.AddressRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    @Override
    public List<AddressDto> getAddressesByUserId(Integer userId) {
        List<Address>addressList=addressRepository.getAddressesByUserId(userId);
        return addressMapper.toAddressDtoList(addressList);

    }

    @Override
    public AddressDto add(AddressAddDto addressAddDto) {
        return addressMapper.toAddressDto(addressRepository.save(addressMapper.toAddress(addressAddDto)));
    }

    @Override
    public AddressDto update(Integer id, AddressAddDto addressAddDto) {
        Address address=addressRepository.findById(id).orElseThrow(IllegalStateException::new);
        if(Objects.nonNull(addressAddDto.getCity())){
            address.setCity(addressAddDto.getCity());
        }
        if(Objects.nonNull(addressAddDto.getRegion())){
            address.setRegion(addressAddDto.getRegion());
        }
        if(Objects.nonNull(addressAddDto.getStreet())){
            address.setStreet(addressAddDto.getStreet());
        }
        if(Objects.nonNull(addressAddDto.getAddressDesc())){
            address.setAddressDesc(addressAddDto.getAddressDesc());
        }
        return addressMapper.toAddressDto(address);
    }

    @Override
    public void delete(Integer id) {
        addressRepository.deleteById(id);
    }
}
