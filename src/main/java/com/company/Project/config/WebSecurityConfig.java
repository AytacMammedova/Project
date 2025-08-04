//package com.company.Project.config;
//
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class WebSecurityConfig {
//    @Bean
//    public static BCryptPasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                )
//                .exceptionHandling(exceptionHandling->exceptionHandling
//                        .authenticationEntryPoint((request, response, authException) ->
//                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)))
//                .httpBasic(Customizer.withDefaults());
//        return http.build();
//    }
//}
////.permitAll-hamiya icaze versin meselen login sehifesi
////authorizeHttpRequests(auth->auth.requestMatchers("/users/*","/users").hasAnyAuthority("ADMIN")
////                        .anyRequest()
////                        .permitAll()