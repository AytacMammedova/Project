package com.company.Project.config;

import com.company.Project.model.entity.Role;
import com.company.Project.model.entity.User;
import com.company.Project.repository.RoleRepository;
import com.company.Project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
    }

    private void initializeRoles() {
        if (roleRepository.findByName("ADMIN") == null) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setRoleDesc("Administrator role with full access");
            roleRepository.save(adminRole);
            log.info("✓ Created ADMIN role");
        }

        if (roleRepository.findByName("USER") == null) {
            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setRoleDesc("Regular user role");
            roleRepository.save(userRole);
            log.info("✓ Created USER role");
        }
    }

    private void initializeAdminUser() {
        String adminEmail = "admin@cartier.com";

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setName("System Administrator");
            admin.setEmail(adminEmail);
            admin.setPhone("+994501234567");
            admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRole(roleRepository.findByName("ADMIN"));

            userRepository.save(admin);
            log.info("========================================");
            log.info("✓ ADMIN USER CREATED SUCCESSFULLY!");
            log.info("Email: {}", adminEmail);
            log.info("Password: Admin123!");
            log.info("CHANGE THIS PASSWORD IMMEDIATELY!");
            log.info("========================================");
        } else {
            log.info("✓ Admin user already exists");
        }
    }
}
