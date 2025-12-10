package com.militaryservices.app.security;

import com.militaryservices.app.enums.MessageKey;
import com.militaryservices.app.service.CustomUserDetailsService;
import com.militaryservices.app.service.MessageService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private MessageService messageService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if ("/api/performLogin".equals(path) || "/api/signUp".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.validateRequest(request)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtUtil.extractUsername(request));
            String token = jwtUtil.extractToken(request);
            List<String> roles = jwtUtil.extractClaim(token, claims -> claims.get("roles", List.class));
            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            ResponseEntity<String> entity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.TOKEN_TAMPERED.key(), Locale.ENGLISH));
            response.setStatus(entity.getStatusCodeValue());
            response.setContentType("application/json");

            String responseBody = String.format("{\"message\": \"%s\"}", entity.getBody().replace("\"", "\\\""));
            response.getWriter().write(responseBody);
        }
    }
}
