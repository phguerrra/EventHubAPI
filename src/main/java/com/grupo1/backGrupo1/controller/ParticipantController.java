package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/events/{eventoId}/participants")
public class ParticipantController {

    private final ParticipantService service;

    public ParticipantController(ParticipantService service) {
        this.service = service;
    }

    /**
     * GET - Lista todos os participantes de um evento
     */
    @GetMapping
    public ResponseEntity<List<Participant>> listarParticipantes(@PathVariable Long eventoId) {
        List<Participant> participants = service.listarParticipantesDoEvento(eventoId);
        return ResponseEntity.ok(participants);
    }

    /**
     * POST - Inscreve um novo participante no evento
     */
    @PostMapping
    public ResponseEntity<?> inscreverParticipante(
            @PathVariable Long eventoId,
            @RequestParam Long userId,
            @RequestBody Participant participant) {
        try {
            Participant novoParticipante = service.inscreverNoEvento(eventoId, participant, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoParticipante);
        } catch (RuntimeException e) {
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
        }
    }

    /**
     * DELETE - Remove um participante do evento
     */
    @DeleteMapping("/{participanteId}")
    public ResponseEntity<?> removerParticipante(@PathVariable Long participanteId) {
        try {
            service.removerParticipante(participanteId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
        }
    }

    /**
     * GET - Verifica se um email já está inscrito no evento
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> verificarEmailInscrito(
            @PathVariable Long eventoId,
            @RequestParam String email) {
        boolean emailInscrito = service.emailJaInscrito(eventoId, email);
        Map<String, Boolean> resposta = new HashMap<>();
        resposta.put("emailInscrito", emailInscrito);
        return ResponseEntity.ok(resposta);
    }
}
