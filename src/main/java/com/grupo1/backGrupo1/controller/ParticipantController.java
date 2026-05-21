package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.PresencaRequestDTO;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.service.ParticipantService;
import com.grupo1.backGrupo1.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Participants", description = "Operações relacionadas a participantes")
@RequestMapping("/events/{eventId}/participants")
public class ParticipantController {

    private final ParticipantService service;
    private final UserService userService;

    public ParticipantController(ParticipantService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    // GET /events/{eventId}/participants
    //   ?status=PENDENTE|APROVADO|REJEITADO  (opcional)
    //   ?orderBy=nome|datainscricao|presenca  (opcional)
    @GetMapping
    @Operation(summary = "Listar participantes de um evento",
            description = "Filtre por status (PENDENTE, APROVADO, REJEITADO) e ordene por nome, datainscricao ou presenca")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada")})
    public ResponseEntity<List<Participant>> listParticipants(
            @PathVariable Long eventId,
            @RequestParam(required = false) Participant.Status status,
            @RequestParam(required = false) String orderBy) {

        List<Participant> participants = service.listParticipantsForEvent(eventId, status, orderBy);
        return ResponseEntity.ok(participants);
    }

    // POST /events/{eventId}/participants
    @PostMapping
    @Operation(summary = "Solicitar inscrição em evento (fica pendente até admin aprovar)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação registrada"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada")
    })
    public ResponseEntity<?> registerParticipant(
            @PathVariable Long eventId,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        Participant saved = service.registerForEvent(eventId, new Participant(), user.getId());

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("id", saved.getId());
        resposta.put("participant", saved);
        resposta.put("mensagem", "Solicitação de inscrição registrada. Aguarde a aprovação do administrador.");

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    // PATCH /events/{eventId}/participants/{id}/aprovar
    // Admin aprova a inscrição
    @PatchMapping("/{participantId}/aprovar")
    @Operation(summary = "Aprovar inscrição de participante (admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscrição aprovada"),
            @ApiResponse(responseCode = "400", description = "Inscrição já aprovada ou evento lotado"),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado")
    })
    public ResponseEntity<Participant> aprovar(
            @PathVariable Long eventId,
            @PathVariable Long participantId) {

        return ResponseEntity.ok(service.aprovarInscricao(eventId, participantId));
    }

    // PATCH /events/{eventId}/participants/{id}/rejeitar
    // Admin rejeita a inscrição
    @PatchMapping("/{participantId}/rejeitar")
    @Operation(summary = "Rejeitar inscrição de participante (admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscrição rejeitada"),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado")
    })
    public ResponseEntity<Participant> rejeitar(
            @PathVariable Long eventId,
            @PathVariable Long participantId) {

        return ResponseEntity.ok(service.rejeitarInscricao(eventId, participantId));
    }

    // PATCH /events/{eventId}/participants/{id}/presenca
    // Admin marca presença no dia do evento
    // Body: { "presenca": "PRESENTE" } ou { "presenca": "AUSENTE" }
    @PatchMapping("/{participantId}/presenca")
    @Operation(summary = "Marcar presença de participante no evento (admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presença registrada"),
            @ApiResponse(responseCode = "400", description = "Participante não está aprovado"),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado")
    })
    public ResponseEntity<Participant> marcarPresenca(
            @PathVariable Long eventId,
            @PathVariable Long participantId,
            @RequestBody @Valid PresencaRequestDTO dto) {

        return ResponseEntity.ok(service.marcarPresenca(eventId, participantId, dto.getPresenca()));
    }

    // DELETE /events/{eventId}/participants/{id}
    @DeleteMapping("/{participantId}")
    @Operation(summary = "Remover participante")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removido"),
            @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    public ResponseEntity<?> removeParticipant(@PathVariable Long participantId) {
        service.removeParticipantById(participantId);
        return ResponseEntity.noContent().build();
    }

    // GET /events/{eventId}/participants/check-email
    @GetMapping("/check-email")
    @Operation(summary = "Checar se e-mail já está inscrito")
    public ResponseEntity<Map<String, Boolean>> checkEmailRegistered(
            @PathVariable Long eventId,
            @RequestParam String email) {

        Map<String, Boolean> resposta = new HashMap<>();
        resposta.put("emailInscrito", service.isEmailRegistered(eventId, email));
        return ResponseEntity.ok(resposta);
    }
}