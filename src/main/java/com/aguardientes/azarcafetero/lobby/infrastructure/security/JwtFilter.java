package com.aguardientes.azarcafetero.lobby.infrastructure.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    // Único endpoint donde aceptamos token vía query param, porque EventSource
    // del navegador no permite enviar headers personalizados.
    private static final String SSE_BALANCE_PATH = "/api/player/balance/live";

    private final SecretKey signingKey;

    public JwtFilter(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token == null) {
            // Sin token: dejamos que Spring Security decida si el endpoint lo permite o no.
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();
            String name = claims.get("name", String.class);
            String avatarUrl = claims.get("avatarUrl", String.class);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(subject, null, List.of());
            auth.setDetails(new JwtAuthDetails(name, avatarUrl));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el JWT desde el header Authorization.
     * Como fallback, lo lee del query param `?token=...` SOLO para el endpoint SSE
     * (EventSource del navegador no soporta headers personalizados).
     */
    private String extractToken(HttpServletRequest request) {
        // 1. Header normal (caso por defecto: todos los endpoints REST)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // 2. Query param, solo para el endpoint SSE
        if (SSE_BALANCE_PATH.equals(request.getRequestURI())) {
            String tokenParam = request.getParameter("token");
            if (tokenParam != null && !tokenParam.isBlank()) {
                return tokenParam;
            }
        }

        return null;
    }
}