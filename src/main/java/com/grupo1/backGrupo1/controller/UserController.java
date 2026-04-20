package com.grupo1.backGrupo1.controller;

import org.springframework.web.bind.annotation.*;
import com.grupo1.backGrupo1.service.UserService;
import com.grupo1.backGrupo1.dto.UserDTO;
import com.grupo1.backGrupo1.dto.LoginDTO;
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

    @PostMapping("/register")
    public User register(@RequestBody UserDTO dto) {
        return service.register(dto);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDTO dto, HttpSession session) {
        User user = service.login(dto);

        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());

        return Map.of(
                "message", "Login realizado com sucesso",
                "userId", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        );
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();
        return Map.of("message", "Logout realizado com sucesso");
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpSession session) {
        Object userId = session.getAttribute("userId");
        Object userRole = session.getAttribute("userRole");

        if (userId == null) {
            throw new RuntimeException("Nenhum usuário logado");
        }

        return Map.of(
                "userId", userId,
                "role", userRole
        );
    }
}