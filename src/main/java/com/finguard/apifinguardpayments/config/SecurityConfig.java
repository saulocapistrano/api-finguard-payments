package com.finguard.apifinguardpayments.config;

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
                .csrf(csrf -> csrf.disable()) // Desativa CSRF para permitir requisições POST e DELETE no Postman
                .cors(cors -> {}) // Habilita CORS para permitir acesso externo (se precisar)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permite acesso a TODOS os endpoints sem autenticação
                );

        return http.build();
    }
}