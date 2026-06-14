package com.server.app.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.app.config.JsonWebToken;
import com.server.app.config.SecurityRules;
import com.server.app.dto.response.ExceptionResponse;
import com.server.app.entities.User;
import com.server.app.exceptions.NotFoundException;
import com.server.app.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JsonWebToken jwtUtil;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JsonWebToken jwtUtil, UserService userService, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return SecurityRules.isPublic(request.getMethod(), request.getRequestURI())
                || SecurityRules.isIgnored(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Bearer token required");
            return;
        }

        try {
            String token = authHeader.substring(7);
            if (jwtUtil.isTokenExpired(token)) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            }

            Integer userId = jwtUtil.extractIdUser(token);
            if (userId == null) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token data invalid");
                return;
            }

            User user = userService.findById(userId);
            if (user.isBlocked()) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Your account has been blocked");
                return;
            }
            if (user.getRole() == null || !Boolean.TRUE.equals(user.getRole().getActive())) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Your account role is not active");
                return;
            }

            Set<GrantedAuthority> authorities = user.getRole().getPermissions() == null
                    ? new HashSet<>()
                    : user.getRole().getPermissions().stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getMethod() + ":" + permission.getPath()))
                    .collect(Collectors.toCollection(HashSet::new));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token data");
            return;
        } catch (NotFoundException e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Your account has been deleted");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), new ExceptionResponse(status, message));
    }
}
