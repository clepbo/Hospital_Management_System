package com.clepbo.hospital_management_system.security;

import com.clepbo.hospital_management_system.staff.entity.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.clepbo.hospital_management_system.staff.entity.Roles.ADMIN;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final String[] UN_SECURED_URL = {
            "/api/v1/labTest/**",
            "/swagger-ui.html/",
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "api/v1/staff/authenticate",
            "/swagger-ui/index.html",
            "/hms/swagger-ui/index.html",
            "/api/v1/seeADoctor/createRequest",
            "api/v1/staff/profilePicture/view/**"

    };
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(UN_SECURED_URL).permitAll()
                                .requestMatchers("/api/v1/staff/**").authenticated()
                                .requestMatchers("/api/v1/seeADoctor/**").authenticated()
                                .requestMatchers("/api/v1/patient/**").authenticated()
                                .requestMatchers("/api/v1/labTest/**").authenticated()
                                .requestMatchers("/api/v1/appointment/**").authenticated()

//                                .requestMatchers("/api/v1/staff/**").hasRole("ADMIN")
//                                .requestMatchers("/api/v1/seeADoctor/createRequest/**").hasAnyRole("PATIENT", "RECEPTIONIST")
//                                .requestMatchers("/api/v1/seeADoctor/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR")
//                                .requestMatchers("/api/v1/patient/address/**").hasRole("RECEPTIONIST")
//                                .requestMatchers("/api/v1/patient/deletePatientAddress/**").hasAnyRole("RECEPTIONIST", "ADMIN")
                                .anyRequest().hasRole(ADMIN.name())
                        )
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                //.httpBasic((basic) -> {});
        return http.build();
    }
}
