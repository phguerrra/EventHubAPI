package com.grupo1.backGrupo1.config;

import com.grupo1.backGrupo1.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // =====================================================
                        // SWAGGER
                        // =====================================================

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // =====================================================
                        // AUTH
                        // =====================================================

                        .requestMatchers(
                                "/users/login",
                                "/users/register",
                                "/users/logout"
                        ).permitAll()

                        // =====================================================
                        // EVENTS
                        // =====================================================

                                .requestMatchers(HttpMethod.GET, "/events", "/events/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/events").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/events/**").hasRole("ADMIN")

// NÃO BLOQUEAR O CANCELAMENTO
                                .requestMatchers(HttpMethod.DELETE, "/events/*/participants/cancel").authenticated()

// SOMENTE EXCLUSÃO DE EVENTOS
                                .requestMatchers(HttpMethod.DELETE, "/events/*").hasRole("ADMIN")
                        // =====================================================
                        // PARTICIPANTS
                        // =====================================================

                        .requestMatchers(HttpMethod.GET,    "/events/*/participants").permitAll()
                        .requestMatchers(HttpMethod.GET,    "/events/*/participants/**").permitAll()
                        .requestMatchers(HttpMethod.POST,   "/events/*/participants").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/events/*/participants/cancel").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/events/*/participants/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/events/*/participants/search").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  "/events/*/participants/**").hasRole("ADMIN")

                        // =====================================================
                        // EVENT MATERIAL
                        // =====================================================

                        .requestMatchers(HttpMethod.GET,    "/events/*/materials").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/events/*/materials").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/events/*/materials/*").hasRole("ADMIN")

                        // =====================================================
                        // AVISOS
                        // =====================================================

                        .requestMatchers(HttpMethod.GET,    "/avisos/**").permitAll()
                        .requestMatchers(HttpMethod.POST,   "/avisos").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/avisos/**").authenticated()

                        // =====================================================
                        // UPLOADS
                        // =====================================================

                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        // =====================================================
                        // TICKETS
                        // =====================================================

                        .requestMatchers(HttpMethod.GET, "/tickets/events/*/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/tickets/validar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/tickets").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/tickets/**").hasRole("ADMIN")

                        // =====================================================
                        // USER
                        // =====================================================

                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/users/me/events").authenticated()

                        // =====================================================
                        // RESTANTE
                        // =====================================================

                        .anyRequest().authenticated()
                )
                .addFilterAfter(jwtAuthenticationFilter, SecurityContextHolderFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
