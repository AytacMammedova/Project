package com.company.Project.service.impl;

import com.company.Project.exceptions.NotFoundException;
import com.company.Project.mapper.AddressMapper;
import com.company.Project.model.dto.request.AddressAddDto;
import com.company.Project.model.dto.AddressDto;
import com.company.Project.model.entity.Address;

import com.company.Project.repository.AddressRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;
@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;


    @Override
    public List<AddressDto> getAddressesByUserId(Long userId) {
        log.debug("Getting addresses for user: {}", userId);
        List<Address>addressList=addressRepository.getAddressesByUserId(userId);
        return addressMapper.toAddressDtoList(addressList);
    }

    @Override
    public AddressDto add(AddressAddDto addressAddDto) {
        log.debug("Creating new address: {}", addressAddDto);
        return addressMapper.toAddressDto(addressRepository.save(addressMapper.toAddress(addressAddDto)));

    }

    @Override
    public AddressDto update(Long id, AddressAddDto addressAddDto) {
        log.debug("Updating address {}: {}", id, addressAddDto);
        Address address=addressRepository.findById(id).orElseThrow(()->new NotFoundException("Address not found with id:" + id));
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
        log.info("Updated address with ID: {}", id);
        return addressMapper.toAddressDto(address);

    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting address with ID: {}", id);
        if (!addressRepository.existsById(id)) {
            throw new NotFoundException("Address not found with id: " + id);
        }
        addressRepository.deleteById(id);
        log.info("Deleted address with ID: {}", id);
    }

    @Override
    public List<AddressDto> getAllAddresses() {
        log.debug("Getting all addresses (admin)");

        List<Address> addresses = addressRepository.findAll();
        return addressMapper.toAddressDtoList(addresses);
    }

    @Override
    public long countAddresses() {
        return addressRepository.count();
    }
}
