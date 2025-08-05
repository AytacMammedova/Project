package com.company.Project.controller;

import com.company.Project.model.dto.AddressDto;
import com.company.Project.model.dto.request.AddressAddDto;
import com.company.Project.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    @GetMapping
    public List<AddressDto> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    @GetMapping("/stats/count")
    public long getAddressCount() {
        return addressService.countAddresses();
    }
}
