package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.NotificationDto;
import com.grupo1.backGrupo1.dto.NotificationResponseDto;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.service.NotificationService;
import com.grupo1.backGrupo1.service.NotificationSseService;
import com.grupo1.backGrupo1.service.ParticipantService;
import com.grupo1.backGrupo1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/avisos")
public class NotificationController {

    private final NotificationService service;
    private final NotificationSseService sseService;
    private final ParticipantService participantService;
    private final UserService userService;

    public NotificationController(NotificationService service,
                                  NotificationSseService sseService,
                                  ParticipantService participantService,
                                  UserService userService) {
        this.service = service;
        this.sseService = sseService;
        this.participantService = participantService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/gerais")
    public ResponseEntity<List<NotificationResponseDto>> listGerais() {
        return ResponseEntity.ok(service.listGerais());
    }

    @GetMapping("/evento/{eventId}")
    public ResponseEntity<List<NotificationResponseDto>> listByEvento(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(service.listByEvento(eventId));
    }

    /**
     * SSE autenticado — filtra notificações pelo usuário logado:
     * - Recebe TODAS as notificações GENERAL
     * - Recebe notificações SPECIFIC_EVENT apenas dos eventos em que está inscrito
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessRuleException("Autenticação necessária para acessar o stream");
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        // Busca os IDs dos eventos em que o usuário está inscrito e aprovado
        Set<Long> subscribedEventIds = participantService
                .listInscricoesByEmail(user.getEmail())
                .stream()
                .filter(p -> p.getStatus() == Participant.Status.APROVADO)
                .map(p -> p.getEvent().getId())
                .collect(Collectors.toSet());

        return sseService.subscribe(email, subscribedEventIds);
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDto> create(
            @RequestBody @Valid NotificationDto dto,
            Authentication authentication) {
        validarAdmin(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid NotificationDto dto,
            Authentication authentication) {
        validarAdmin(authentication);
        return ResponseEntity.ok(service.update(id, dto));
    }

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