package com.company.Project.controller;

import com.company.Project.model.dto.request.ResetPasswordRequestDto;
import com.company.Project.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        return passwordResetService.resetPassword(request);
    }
}
