package com.company.Project.model.dto.request;

import com.company.Project.model.entity.Address;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddDto {

    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private List<AddressAddDto> addresses;
}
