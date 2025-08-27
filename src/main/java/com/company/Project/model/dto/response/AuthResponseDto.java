package com.company.Project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private String email;
    private String name;
    private String role;

    public AuthResponseDto(String accessToken, String email, String name, String role) {
        this.accessToken = accessToken;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}