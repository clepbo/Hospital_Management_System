package com.clepbo.hospital_management_system.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private static final String[] UN_SECURED_URL = {
            "/api/v1/staff/create",
            "/swagger-ui.html/"
    };
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(UN_SECURED_URL).permitAll()
                                .requestMatchers("/api/v1/staff/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/seeADoctor/createRequest/**").hasAnyRole("PATIENT", "RECEPTIONIST")
                                .requestMatchers("/api/v1/seeADoctor/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR")
                                .requestMatchers("/api/v1/patient/address/**").hasRole("RECEPTIONIST")
                                .requestMatchers("/api/v1/patient/deletePatientAddress/**").hasAnyRole("RECEPTIONIST", "ADMIN")
                                .anyRequest().hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_PATIENT")
                        ).formLogin(withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return customUserDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        return daoAuthenticationProvider;
    }
}
