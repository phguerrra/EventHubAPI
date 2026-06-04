package com.grupo1.backGrupo1.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("====================================");
        System.out.println("URL: " + request.getRequestURI());
        System.out.println("METHOD: " + request.getMethod());
        System.out.println("AUTH HEADER: " + request.getHeader("Authorization"));
        System.out.println("====================================");

        try {

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                String token = authHeader.substring(7);

                Claims claims = jwtService.validateToken(token);

                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                Long userId = claims.get("userId", Long.class);

                System.out.println("EMAIL TOKEN: " + email);
                System.out.println("ROLE TOKEN: " + role);
                System.out.println("USER ID TOKEN: " + userId);

                List<SimpleGrantedAuthority> authorities =
                        "ADMIN".equals(role)
                                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                : List.of(new SimpleGrantedAuthority("ROLE_USER"));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                authorities
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println("AUTENTICADO COM SUCESSO");
                System.out.println(
                        SecurityContextHolder.getContext().getAuthentication()
                );
            }

        } catch (Exception e) {

            System.out.println("ERRO JWT:");
            e.printStackTrace();

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}