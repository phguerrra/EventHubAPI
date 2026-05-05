package com.grupo1.backGrupo1.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtService.validateToken(token);
                String principal = claims.getSubject();
                String role = claims.get("role", String.class);

                List<SimpleGrantedAuthority> authorities =
                        "ADMIN".equals(role)
                                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                : List.of(new SimpleGrantedAuthority("ROLE_USER"));

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                principal, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // token inválido — deixa passar sem autenticação
            }
        }

        filterChain.doFilter(request, response);
    }
}