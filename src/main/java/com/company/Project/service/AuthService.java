package com.company.Project.service;

import com.company.Project.model.dto.request.LoginDto;
import com.company.Project.model.dto.request.RegisterDto;
import com.company.Project.model.dto.response.AuthResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponseDto login(LoginDto loginDto);
    AuthResponseDto register(RegisterDto registerDto);
}