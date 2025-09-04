package com.company.Project.model.dto;

import com.company.Project.model.entity.Address;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    @Valid
    private List<AddressDto> addresses;
}
