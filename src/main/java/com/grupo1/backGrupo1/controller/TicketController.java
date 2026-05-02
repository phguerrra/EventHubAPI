package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.CreateTicketRequestDTO;
import com.grupo1.backGrupo1.dto.TicketResponseDTO;
import com.grupo1.backGrupo1.dto.ValidacaoRequestDTO;
import com.grupo1.backGrupo1.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@Validated
public class TicketController {

    private final TicketService ticketService;
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody CreateTicketRequestDTO req) {
        TicketResponseDTO resp = ticketService.createTicket(req.getEventId(), req.getExpirationMinutes());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validarTicket(@RequestBody ValidacaoRequestDTO req) {
        ticketService.validateAndUseTicket(req.getToken());
        return ResponseEntity.ok().body(java.util.Map.of("message", "Ticket válido e marcado como usado"));
    }
}
