package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.CreateTicketRequestDTO;
import com.grupo1.backGrupo1.dto.TicketResponseDTO;
import com.grupo1.backGrupo1.dto.ValidacaoRequestDTO;
import com.grupo1.backGrupo1.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tickets")
@Validated
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Cria o ingresso vinculado a um participante específico
    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(
            @RequestBody @Valid CreateTicketRequestDTO req
    ) {
        TicketResponseDTO resp = ticketService.createTicket(
                req.getEventId(),
                req.getParticipantId(),
                req.getExpirationMinutes()
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/events/{eventId}/me")
    public ResponseEntity<TicketResponseDTO> getMyTicket(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                ticketService.getTicketForParticipant(eventId, authentication.getName())
        );
    }

    // Valida o QR e marca presença do participante automaticamente
    @PostMapping("/validar")
    public ResponseEntity<?> validarTicket(
            @RequestBody @Valid ValidacaoRequestDTO req
    ) {
        ticketService.validateAndUseTicket(req.getToken());
        return ResponseEntity.ok(
                Map.of("message", "Ticket válido, presença registrada com sucesso")
        );
    }
}
