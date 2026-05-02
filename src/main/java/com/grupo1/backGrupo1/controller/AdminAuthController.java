package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.AdminLoginDTO;
import com.grupo1.backGrupo1.service.TicketJwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final TicketJwtService jwtService;

    public AdminAuthController(AuthenticationManager authenticationManager, TicketJwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginDTO dto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );
            // generate admin token
            String token = jwtService.generateAdminToken(dto.getUsername(), dto.getExpirationMinutes() * 60 * 1000);
            return ResponseEntity.ok(java.util.Map.of("token", token));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            // Return consistent message in Portuguese matching app style
            return ResponseEntity.status(401).body(java.util.Map.of("erro", "Usuário inexistente ou senha inválida"));
        }
    }
}
