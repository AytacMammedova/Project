package com.company.Project.service.impl;

import com.company.Project.exceptions.PasswordValidationException;
import com.company.Project.exceptions.UserNotFoundException;
import com.company.Project.model.dto.request.ResetPasswordRequestDto;
import com.company.Project.model.entity.PasswordResetToken;
import com.company.Project.model.entity.User;
import com.company.Project.repository.PasswordResetTokenRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int TOKEN_LENGTH = 6;
    private static final int EXPIRY_MINUTES = 15;

    @Override
    @Transactional
    public String resetPassword(ResetPasswordRequestDto request) {
        if (request.getToken() == null || request.getToken().trim().isEmpty()) {
            return generateAndSendToken(request.getEmail());
        }
        return resetPasswordWithToken(request);
    }

    private String generateAndSendToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Optional<PasswordResetToken> existingToken = tokenRepository.findByEmailAndUsedFalse(email);
        if (existingToken.isPresent()) {
            existingToken.get().setUsed(true);
            tokenRepository.save(existingToken.get());
        }
        String resetToken = generateResetToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(EXPIRY_MINUTES);

        PasswordResetToken passwordResetToken = new PasswordResetToken(resetToken, email, expiryDate);
        tokenRepository.save(passwordResetToken);

        log.info("==========================================");
        log.info("PASSWORD RESET TOKEN FOR: {}", email);
        log.info("Your reset code: {}", resetToken);
        log.info("Expires in {} minutes", EXPIRY_MINUTES);
        log.info("Use this code to reset your password");
        log.info("==========================================");

        return "Reset code sent! Check console for your 6-digit code. It expires in " + EXPIRY_MINUTES + " minutes.";
    }

    private String resetPasswordWithToken(ResetPasswordRequestDto request) {
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new PasswordValidationException("New password is required");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            throw new PasswordValidationException("Password confirmation is required");
        }

        PasswordResetToken resetToken = tokenRepository.findByTokenAndUsedFalse(request.getToken())
                .orElseThrow(() -> new PasswordValidationException("Invalid or expired reset code"));

        if (resetToken.isExpired()) {
            throw new PasswordValidationException("Reset code has expired. Please request a new one.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordValidationException("New password and confirmation do not match");
        }
        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successfully for user: {}", user.getEmail());
        return "Password reset successfully! You can now login with your new password.";
    }

    private String generateResetToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(random.nextInt(10));
        }

        return token.toString();
    }
}
