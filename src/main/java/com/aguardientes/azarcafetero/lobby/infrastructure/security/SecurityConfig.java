package com.aguardientes.azarcafetero.lobby.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Value("${internal.api.key}")
    private String internalApiKey;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Health check para ALB
                        .requestMatchers("/actuator/health").permitAll()
                        // Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Endpoints públicos
                        .requestMatchers("/api/building/layout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/player/*/internal").permitAll()
                        // Endpoints de juego protegidos por X-Internal-Key (no JWT)
                        .requestMatchers(HttpMethod.POST, "/api/player/bet", "/api/player/win", "/api/player/loss")
                        .access((authentication, context) -> {
                            String key = context.getRequest().getHeader("X-Internal-Key");
                            return new AuthorizationDecision(internalApiKey.equals(key));
                        })
                        // Todo lo demás requiere JWT
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) ->
                                res.sendError(401, "Unauthorized: valid JWT required"))
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}