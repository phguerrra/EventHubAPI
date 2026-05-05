package com.grupo1.backGrupo1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo1.backGrupo1.service.UserService;
import com.grupo1.backGrupo1.dto.UserDTO;
import com.grupo1.backGrupo1.dto.LoginDTO;
import com.grupo1.backGrupo1.dto.LoginResponseDTO;
import com.grupo1.backGrupo1.dto.UserResponseDTO;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.security.JwtService;

import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final JwtService jwtService;

    public UserController(UserService service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    // REGISTER
    @PostMapping("/register")
    public User register(@RequestBody @Valid UserDTO dto) {
        return service.register(dto);
    }


    // LOGIN (COM TRATAMENTO DE ERRO)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO dto) {
        try {
            User user = service.login(dto);
            String token = jwtService.generateToken(user);

            boolean isAdmin = service.isAdmin(user);

            return ResponseEntity.ok(new LoginResponseDTO(
                    "Login realizado com sucesso",
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    isAdmin,
                    token,
                    null
            ));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // LOGOUT (kept for compatibility but stateless apps may not use it)
    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Map.of("message", "Logout realizado com sucesso");
    }

    // USUÁRIO LOGADO via JWT
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        String email = authentication.getName();
        User user = service.findByEmail(email);

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone() != null ? user.getPhone() : "");
        response.put("cpf", user.getCpf() != null ? user.getCpf() : "");
        response.put("role", user.getRole());
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }

    // AREA ADMIN (NOVO)
    @PostMapping("/admin/dashboard")
    public ResponseEntity<?> areaAdmin(Authentication authentication) {

        User user = service.findByEmail(authentication.getName());
        boolean isAdmin = service.isAdmin(user);

        if (!isAdmin) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado");
        }

        return ResponseEntity.ok("Bem-vindo admin");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken) {

        String email = jwtService.extractUsername(refreshToken);

        User user = service.findByEmail(email);

        String newToken = jwtService.generateToken(user);

        return ResponseEntity.ok(newToken);
    }

}
