package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.NotificationDto;
import com.grupo1.backGrupo1.dto.NotificationResponseDto;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avisos")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // GET /avisos — todos (gerais + eventos)
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    // GET /avisos/gerais — só avisos gerais da plataforma
    @GetMapping("/gerais")
    public ResponseEntity<List<NotificationResponseDto>> listGerais() {
        return ResponseEntity.ok(service.listGerais());
    }

    // GET /avisos/evento/{eventId} — avisos de um evento específico
    @GetMapping("/evento/{eventId}")
    public ResponseEntity<List<NotificationResponseDto>> listByEvento(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(service.listByEvento(eventId));
    }

    // POST /avisos — cria aviso (ADMIN)
    @PostMapping
    public ResponseEntity<NotificationResponseDto> create(
            @RequestBody @Valid NotificationDto dto,
            Authentication authentication) {

        validarAdmin(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    // DELETE /avisos/{id} — deleta aviso (ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {

        validarAdmin(authentication);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void validarAdmin(Authentication authentication) {
        if (authentication == null) {
            throw new BusinessRuleException("Usuário não autenticado");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new BusinessRuleException(
                    "Apenas administradores podem realizar esta ação");
        }
    }

}
