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
                .csrf(csrf -> csrf.disable()) // Desativa CSRF para permitir POST e DELETE sem erro 403
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/payments/**").permitAll() // Permite acesso a todos os endpoints sem autenticação
                        .anyRequest().authenticated()
                )
                .httpBasic(); // Habilita autenticação básica (se necessário)

        return http.build();
    }
}
