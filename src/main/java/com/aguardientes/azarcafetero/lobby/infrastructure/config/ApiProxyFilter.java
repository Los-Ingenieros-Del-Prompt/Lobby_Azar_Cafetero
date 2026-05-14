
java
package com.aguardientes.azarcafetero.lobby.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiProxyFilter extends OncePerRequestFilter {

    private static final String PROXY_PREFIX = "/api-proxy";
    private static final String API_PREFIX = "/api";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (uri.startsWith(PROXY_PREFIX)) {
            String newUri = API_PREFIX + uri.substring(PROXY_PREFIX.length());

            HttpServletRequestWrapper wrapped = new HttpServletRequestWrapper(request) {
                @Override
                public String getRequestURI() {
                    return newUri;
                }

                @Override
                public String getServletPath() {
                    return newUri;
                }
            };

            filterChain.doFilter(wrapped, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}