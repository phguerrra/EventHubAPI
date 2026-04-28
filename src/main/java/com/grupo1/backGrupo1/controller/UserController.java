package com.grupo1.backGrupo1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo1.backGrupo1.service.UserService;
import com.grupo1.backGrupo1.dto.UserDTO;
import com.grupo1.backGrupo1.dto.LoginDTO;
import com.grupo1.backGrupo1.dto.LoginResponseDTO;
import com.grupo1.backGrupo1.model.User;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public User register(@RequestBody UserDTO dto) {
        return service.register(dto);
    }

    // =========================
    // LOGIN (COM TRATAMENTO DE ERRO)
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto, HttpSession session) {
        try {
            User user = service.login(dto);

            boolean isAdmin = service.isAdmin(user);

            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("isAdmin", isAdmin);

            return ResponseEntity.ok(new LoginResponseDTO(
                    "Login realizado com sucesso",
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    isAdmin
            ));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================
    // LOGOUT
    // =========================
    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();
        return Map.of("message", "Logout realizado com sucesso");
    }

    // =========================
    // USUÁRIO LOGADO
    // =========================
    @GetMapping("/me")
    public Map<String, Object> me(HttpSession session) {

        Object userId = session.getAttribute("userId");
        Object userRole = session.getAttribute("userRole");
        Object isAdmin = session.getAttribute("isAdmin");

        if (userId == null) {
            throw new RuntimeException("Nenhum usuário logado");
        }

        return Map.of(
                "userId", userId,
                "role", userRole,
                "isAdmin", isAdmin != null ? isAdmin : false
        );
    }

    // =========================
    // 🔥 AREA ADMIN (NOVO)
    // =========================
    @PostMapping("/admin/dashboard")
    public ResponseEntity<?> areaAdmin(HttpSession session) {

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (isAdmin == null || !isAdmin) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado");
        }

        return ResponseEntity.ok("Bem-vindo admin");
    }
}