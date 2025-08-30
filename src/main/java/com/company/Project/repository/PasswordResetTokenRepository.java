package com.company.Project.repository;

import com.company.Project.model.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    Optional<PasswordResetToken> findByEmailAndUsedFalse(String email);

    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate < :now OR p.used = true")
    void deleteExpiredAndUsedTokens(LocalDateTime now);}
