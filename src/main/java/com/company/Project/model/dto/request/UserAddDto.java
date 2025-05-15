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
    @NotBlank(message = "Name field is required")
    private String name;

    @NotBlank(message = "Username field is required")
    private String username;

    @NotBlank(message = "Email field is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Phone field is required")
    @Pattern(
            regexp = "^\\+?994(10|50|51|55|70|77)\\d{7}$",
            message = "Invalid Azerbaijan mobile number"
    )
    private String phone;
    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth field is required")
    private LocalDate dateOfBirth;

    private List<AddressAddDto> addresses;
}
