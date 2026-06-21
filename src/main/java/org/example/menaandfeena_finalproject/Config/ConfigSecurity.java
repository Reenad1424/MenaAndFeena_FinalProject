package org.example.menaandfeena_finalproject.Config;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Service.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ConfigSecurity {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC
                        // =========================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/users/contact"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/users/welcome",
                                "/api/v1/users/about"
                        ).permitAll()

                        // =========================
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                "/api/v1/users/get-all",
                                "/api/v1/users/add",
                                "/api/v1/users/update/**",
                                "/api/v1/users/delete/**",
                                "/api/v1/landmarks/get-all",
                                "/api/v1/landmarks/add",
                                "/api/v1/landmarks/update/**",
                                "/api/v1/landmarks/delete/**",
                                "/api/v1/election-rounds/add",
                                "/api/v1/election-rounds/update/**",
                                "/api/v1/election-rounds/delete/**",
                                "/api/v1/mayor-candidates/update/**",
                                "/api/v1/mayor-candidates/delete/**",
                                "/api/v1/mayor-profiles/get-all",
                                "/api/v1/mayor-profiles/add",
                                "/api/v1/mayor-profiles/update/**",
                                "/api/v1/mayor-profiles/delete/**",
                                "/api/v1/mayor-votes/get-all",
                                "/api/v1/mayor-votes/delete/**",
                                "/api/v1/neighborhoods/get-all",
                                "/api/v1/neighborhoods/add",
                                "/api/v1/neighborhoods/update/**",
                                "/api/v1/neighborhoods/delete/**"
                        ).hasAuthority("ADMIN")

                        // =========================
                        // USER + MAYOR
                        // =========================

                        .requestMatchers(
                                "/api/v1/users/**",
                                "/api/v1/neighborhoods/**",
                                "/api/v1/landmarks/**",
                                "/api/v1/mayor-candidates/**",
                                "/api/v1/mayor-votes/**",
                                "/api/v1/election-rounds/get-all",
                                "/api/v1/election-rounds/get/**",
                                "/api/v1/family-members/**",
                                "/api/v1/landmarks/sync",
                                "/api/v1/landmarks/nearby",
                                "/api/v1/landmarks/dashboard",
                                "/api/v1/mayor-votes/vote/**",
                                "/api/v1/mayor-candidates/get-all",
                                "/api/v1/mayor-candidates/round/**",
                                "/api/v1/mayor-candidates/profile/**",
                                "/api/v1/mayor-candidates/apply/round/**",
                                "/api/v1/neighborhoods/dashboard"
                        ).hasAnyAuthority("USER", "MAYOR")

                        // =========================
                        // MAYOR ONLY
                        // =========================

                        .requestMatchers(
                                "/api/v1/mayor-profiles/**",
                                "/api/v1/mayor-reports/**",
                                "/api/v1/mayor-profiles/analytics",
                                "/api/v1/mayor-profiles/reports",
                                "/api/v1/mayor-profiles/weekly",
                                "/api/v1/mayor-profiles/performance",
                                "/api/v1/mayor-profiles/satisfaction",
                                "/api/v1/mayor-profiles/resend-appointment-email"
                        ).hasAuthority("MAYOR")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}