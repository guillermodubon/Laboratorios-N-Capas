package com.server.app.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.app.config.SecurityRules;
import com.server.app.dto.response.ExceptionResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class DynamicAuthorizationFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper;

    public DynamicAuthorizationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if (SecurityRules.isIgnored(path) || SecurityRules.isPublic(method, path)
                || SecurityRules.isAuthOnly(method, path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        if (!isAuthorized(authentication, method, path)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Acceso denegado: no tienes permisos para esta ruta: " + path);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthorized(Authentication authentication, String method, String path) {
        return authentication.getAuthorities().stream().anyMatch(authority -> {
            String[] parts = authority.getAuthority().split(":", 2);
            return parts.length == 2
                    && method.equalsIgnoreCase(parts[0])
                    && pathMatcher.match(parts[1], path);
        });
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), new ExceptionResponse(status, message));
    }
}
