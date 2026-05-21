package com.grupo1.backGrupo1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.service.ParticipantService;
import com.grupo1.backGrupo1.service.UserService;
import com.grupo1.backGrupo1.dto.UserDTO;
import com.grupo1.backGrupo1.dto.LoginDTO;
import com.grupo1.backGrupo1.dto.LoginResponseDTO;
import com.grupo1.backGrupo1.security.JwtService;

import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final JwtService jwtService;
    private final ParticipantService participantService;

    public UserController(UserService service, JwtService jwtService, ParticipantService participantService) {
        this.service = service;
        this.jwtService = jwtService;
        this.participantService = participantService;
    }

    @PostMapping("/register")
    public User register(@RequestBody @Valid UserDTO dto) {
        return service.register(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto, jakarta.servlet.http.HttpSession session) {
        try {
            User user = service.login(dto);
            String token = jwtService.generateToken(user);
            boolean isAdmin = service.isAdmin(user);

            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("isAdmin", isAdmin);

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

    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Map.of("message", "Logout realizado com sucesso");
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        String email = authentication.getName();
        User user = service.findByEmail(email);

        Map<String, Object> response = new HashMap<>();
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone() != null ? user.getPhone() : "");
        response.put("cpf", user.getCpf() != null ? user.getCpf() : "");
        response.put("role", user.getRole());
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }

    // Eventos que o usuário está inscrito (todos os status)
    @GetMapping("/me/events")
    public ResponseEntity<List<Participant>> myEvents(Authentication authentication) {
        String email = authentication.getName();
        List<Participant> inscricoes = participantService.listInscricoesByEmail(email);
        return ResponseEntity.ok(inscricoes);
    }

    @PostMapping("/admin/dashboard")
    public ResponseEntity<?> areaAdmin(jakarta.servlet.http.HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
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