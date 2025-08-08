package com.company.Project.model.dto.request;

import com.company.Project.model.entity.Address;
import com.company.Project.model.entity.Bucket;
import com.company.Project.model.entity.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
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
    @Size(min = 2, max = 100, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
    private String name;

    @NotBlank(message = "Email field is required")
    @Email(message = "Email format is invalid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
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

    @Valid
    private List<Address>addresses;

}
