package com.company.Project.service;


import com.company.Project.model.dto.request.AddressAddDto;
import com.company.Project.model.dto.AddressDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AddressService {

    List<AddressDto> getAddressesByUserId(Integer userId);
    AddressDto add(AddressAddDto addressAddDto);
    AddressDto update(Integer id, AddressAddDto addressAddDto);
    void delete(Integer id);
}
