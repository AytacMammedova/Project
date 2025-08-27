package com.company.Project.service.impl;

import com.company.Project.exceptions.AlreadyExistsException;
import com.company.Project.model.dto.request.LoginDto;
import com.company.Project.model.dto.request.RegisterDto;
import com.company.Project.model.dto.response.AuthResponseDto;
import com.company.Project.model.entity.User;
import com.company.Project.repository.RoleRepository;
import com.company.Project.repository.UserRepository;
import com.company.Project.security.JwtUtil;
import com.company.Project.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponseDto login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            String jwt = jwtUtil.generateToken(authentication.getName());

            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("User {} logged in successfully", loginDto.getEmail());

            return new AuthResponseDto(
                    jwt,
                    user.getEmail(),
                    user.getName(),
                    user.getRole().getName()
            );

        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginDto.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
    }

    @Override
    @Transactional
    public AuthResponseDto register(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new AlreadyExistsException("User already exists with email: " + registerDto.getEmail());
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPhone(registerDto.getPhone());
        user.setDateOfBirth(registerDto.getDateOfBirth());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(roleRepository.findByName("USER"));

        User savedUser = userRepository.save(user);
        String jwt = jwtUtil.generateToken(savedUser.getEmail());

        log.info("User {} registered successfully", registerDto.getEmail());

        return new AuthResponseDto(
                jwt,
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole().getName()
        );
    }
}