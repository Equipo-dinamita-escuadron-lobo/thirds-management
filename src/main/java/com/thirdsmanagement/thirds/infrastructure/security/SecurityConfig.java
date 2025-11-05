package com.thirdsmanagement.thirds.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * @brief Configuración central de Spring Security con JWT y multi-tenancy
 *
 * Configura seguridad stateless con OAuth2 Resource Server, JWT authentication,
 * deshabilitación de CSRF, y habilita @PreAuthorize/@Secured en métodos.
 * Define reglas de autorización para endpoints públicos vs protegidos.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private JwtAuthConverter jwtAuthConverter;

    /**
     * @brief Configura cadena de filtros de seguridad HTTP con JWT OAuth2
     * @details Deshabilita CSRF, configura endpoints públicos (Swagger, actuator),
     * establece servidor de recursos OAuth2 con convertidor JWT personalizado,
     * y política de sesión stateless para API REST.
     * @param httpSecurity configuración de seguridad HTTP de Spring
     * @return SecurityFilterChain configurado con reglas de autorización
     * @throws Exception si hay errores en la configuración
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(http -> http
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**","/actuator/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> {
                    oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter));
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
