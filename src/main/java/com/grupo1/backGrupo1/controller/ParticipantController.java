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
@RequestMapping("/events/{eventId}/participants")
public class ParticipantController {

    private final ParticipantService service;

    public ParticipantController(ParticipantService service) {
        this.service = service;
    }

    // get
    @GetMapping
    public ResponseEntity<List<Participant>> listParticipants(@PathVariable Long eventId) {
        List<Participant> participants = service.listParticipantsForEvent(eventId);
        return ResponseEntity.ok(participants);
    }

    // post
    @PostMapping
    public ResponseEntity<?> registerParticipant(
            @PathVariable Long eventId,
            @RequestParam Long userId,
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid com.grupo1.backGrupo1.dto.ParticipantDTO dto) {
        com.grupo1.backGrupo1.model.Participant participant = new com.grupo1.backGrupo1.model.Participant();
        participant.setName(dto.getName());
        participant.setEmail(dto.getEmail());
        participant.setPhone(dto.getPhone());
        participant.setCpf(dto.getCpf());
        com.grupo1.backGrupo1.model.Participant newParticipant = service.registerForEvent(eventId, participant, userId);
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("id", newParticipant.getId());
        resposta.put("participant", newParticipant);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    // Delete
    @DeleteMapping("/{participantId}")
    public ResponseEntity<?> removeParticipant(@PathVariable Long participantId) {
        service.removeParticipantById(participantId);
        return ResponseEntity.noContent().build();
    }

    // get
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailRegistered(
            @PathVariable Long eventId,
            @RequestParam String email) {
        boolean emailRegistered = service.isEmailRegistered(eventId, email);
        Map<String, Boolean> resposta = new HashMap<>();
        resposta.put("emailInscrito", emailRegistered);
        return ResponseEntity.ok(resposta);
    }
}
