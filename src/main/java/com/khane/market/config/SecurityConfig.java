package com.khane.market.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for webhook endpoints
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/webhooks/**")
                        .permitAll()  // Allow webhook without authentication
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}

