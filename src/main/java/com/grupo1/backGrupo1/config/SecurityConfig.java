package com.grupo1.backGrupo1.config;

import com.grupo1.backGrupo1.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

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

                        .requestMatchers(
                                HttpMethod.GET,
                                "/events",
                                "/events/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/events"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/events/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/events/**"
                        ).hasRole("ADMIN")

                        // =====================================================
                        // PARTICIPANTS
                        // =====================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/events/*/participants"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/events/*/participants/**"
                        ).permitAll()

                        // INSCREVER
                        .requestMatchers(
                                HttpMethod.POST,
                                "/events/*/participants"
                        ).authenticated()

                        // CANCELAR INSCRIÇÃO
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/events/*/participants/cancel"
                        ).authenticated()

                        // ADMIN PARTICIPANTS
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/events/*/participants/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/events/*/participants/search"
                        ).hasRole("ADMIN")

                        // =====================================================
                        // EVENT MATERIAL
                        // =====================================================
                        .requestMatchers(HttpMethod.GET,  "/events/*/materials"

                        ).authenticated()
                        .requestMatchers(HttpMethod.POST, "/events/*/materials"

                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/events/*/materials/*"

                        ).hasRole("ADMIN")

                        // =====================================================
                        // AVISOS
                        // =====================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/avisos/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/avisos"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/avisos/**"
                        ).authenticated()

                        // =====================================================
                        // SEARCH
                        // =====================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/events/search"
                        ).permitAll()

                        // =====================================================
                        // PALESTRANTES
                        // =====================================================

                        .requestMatchers(
                                "/palestrantes/**"
                        ).permitAll()

                        // =====================================================
                        // UPLOADS
                        // =====================================================

                        .requestMatchers(
                                "/uploads/**"
                        ).permitAll()

                        // =====================================================
                        // TICKETS
                        // =====================================================

                        .requestMatchers(
                                "/tickets/**"
                        ).authenticated()

                        // =====================================================
                        // USER
                        // =====================================================

                        .requestMatchers(
                                "/users/me"
                        ).authenticated()

                        .requestMatchers(
                                "/users/me/events"
                        ).authenticated()

                        // =====================================================
                        // RESTANTE
                        // =====================================================

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(
                List.of("http://localhost:3000")
        );

        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS",
                        "PATCH"
                )
        );

        config.setAllowedHeaders(
                List.of("*")
        );

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }

    @Bean
    public InMemoryUserDetailsManager users(
            PasswordEncoder passwordEncoder
    ) {

        UserDetails admin = User.builder()
                .username("admin")
                .password(
                        passwordEncoder.encode("adminpass")
                )
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}