package com.company.Project.config;

import com.company.Project.security.JwtAccessDeniedHandler;
import com.company.Project.security.JwtAuthenticationEntryPoint;
import com.company.Project.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;


    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/size-guide/**",
            "/images/**"

    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .requestMatchers("/images/*").permitAll()
                        .requestMatchers("/images/*/*").permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                        // ===== PRODUCTS - GET PUBLIC, MODIFY ADMIN ONLY =====
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers("/products/**").hasRole("ADMIN")

                        // ===== CATEGORIES - GET PUBLIC, MODIFY ADMIN ONLY =====
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers("/categories/**").hasRole("ADMIN")

                        // ===== PRODUCT TYPES - GET PUBLIC, MODIFY ADMIN ONLY =====
                        .requestMatchers(HttpMethod.GET, "/productTypes/**").permitAll()
                        .requestMatchers("/productTypes/**").hasRole("ADMIN")

                        // ===== SUBTYPES - GET PUBLIC, MODIFY ADMIN ONLY =====
                        .requestMatchers(HttpMethod.GET, "/subtypes/**").permitAll()
                        .requestMatchers("/subtypes/**").hasRole("ADMIN")

                        // ===== PAYMENTS - GET ADMIN ONLY, MODIFY AUTHENTICATED =====
                        .requestMatchers(HttpMethod.GET, "/payments/**").hasRole("ADMIN")
                        .requestMatchers("/payments/**").authenticated()

                        // ===== USERS - ADMIN ONLY FOR ALL OPERATIONS =====
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // ===== PROFILE - AUTHENTICATED USERS (SELF-SERVICE) =====
                        .requestMatchers("/profile/**").authenticated()

                        // ===== BUCKETS - AUTHENTICATED USERS (MANAGE OWN CART) =====
                        .requestMatchers("/buckets/**").authenticated()

                        // ===== ADDRESSES - GET ADMIN ONLY, MODIFY AUTHENTICATED =====
                        .requestMatchers(HttpMethod.GET, "/addresses/**").hasRole("ADMIN")
                        .requestMatchers("/addresses/**").authenticated()

                        // ===== ALL OTHER REQUESTS NEED AUTHENTICATION =====
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}