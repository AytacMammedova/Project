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
    @GetMapping("/{userId}")
    public List<AddressDto> getAddressesByUserId(@PathVariable Integer userId){
        return addressService.getAddressesByUserId(userId);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDto add(@RequestBody AddressAddDto addressAddDto){
        return addressService.add(addressAddDto);
    }
    @PutMapping("/{id}")
    public AddressDto update(@PathVariable Integer id,@RequestBody AddressAddDto addressAddDto){
        return addressService.update(id, addressAddDto);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id){
        addressService.delete(id);
    }
}
