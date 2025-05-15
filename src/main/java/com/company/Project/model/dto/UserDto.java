package com.company.Project.model.dto;

import com.company.Project.model.entity.Address;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class UserDto {
    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private List<Address> addresses;
}
