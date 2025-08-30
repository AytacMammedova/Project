package com.company.Project.service;

import com.company.Project.model.dto.request.ResetPasswordRequestDto;

public interface PasswordResetService {
    String resetPassword(ResetPasswordRequestDto request);
}
