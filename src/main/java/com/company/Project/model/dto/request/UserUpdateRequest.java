package com.company.Project.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateRequest {
    @Schema(description = "User name", example = "Ali Aliyev")
    private String name;

    @Schema(description = "User email", example = "ali.aliyev@gamil.com")
    private String email;

    @Schema(description = "User phone", example = "+994501234567")
    private String phone;

    @Schema(description = "Date of birth", example = "1990-01-15")
    private LocalDate dateOfBirth;

}
