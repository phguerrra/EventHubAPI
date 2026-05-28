package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.ParticipantResponseDTO;
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
import java.util.stream.Collectors;

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

    // =========================================================
    // LISTAR PARTICIPANTES
    // =========================================================

    @GetMapping
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada")})
    public ResponseEntity<List<ParticipantResponseDTO>> listParticipants(
            @PathVariable Long eventId,
            @RequestParam(required = false) Participant.Status status,
            @RequestParam(required = false) String orderBy
    ) {
        return ResponseEntity.ok(
                service.listParticipantsForEvent(eventId, status, orderBy)
                        .stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    // =========================================================
    // BUSCAR PARTICIPANTES
    // =========================================================

    @GetMapping("/search")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Resultados retornados")})
    public ResponseEntity<List<ParticipantResponseDTO>> searchParticipants(
            @PathVariable Long eventId,
            @RequestParam(required = false) String q
    ) {
        return ResponseEntity.ok(
                service.searchParticipants(eventId, q)
                        .stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    // =========================================================
    // INSCREVER PARTICIPANTE
    // =========================================================

    @PostMapping
    @Operation(summary = "Solicitar inscrição em evento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação registrada"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada")
    })
    public ResponseEntity<?> registerParticipant(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        Participant saved = service.registerForEvent(eventId, new Participant(), user.getId());

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("id", saved.getId());
        resposta.put("participant", toDTO(saved));
        resposta.put("mensagem", "Solicitação de inscrição registrada. Aguarde aprovação.");

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    // =========================================================
    // CANCELAR INSCRIÇÃO
    // =========================================================

    @DeleteMapping("/cancel")
    @Operation(summary = "Cancelar inscrição do usuário logado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscrição cancelada"),
            @ApiResponse(responseCode = "404", description = "Inscrição não encontrada")
    })
    public ResponseEntity<?> cancelarInscricao(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        service.cancelarInscricao(authentication.getName(), eventId);
        return ResponseEntity.ok(Map.of("mensagem", "Inscrição cancelada com sucesso"));
    }

    // =========================================================
    // APROVAR INSCRIÇÃO
    // =========================================================

    @PatchMapping("/{participantId}/aprovar")
    @Operation(summary = "Aprovar inscrição")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscrição aprovada"),
            @ApiResponse(responseCode = "400", description = "Evento lotado"),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado")
    })
    public ResponseEntity<ParticipantResponseDTO> aprovar(
            @PathVariable Long eventId,
            @PathVariable Long participantId
    ) {
        return ResponseEntity.ok(toDTO(service.aprovarInscricao(eventId, participantId)));
    }

    // =========================================================
    // REJEITAR INSCRIÇÃO
    // =========================================================

    @PatchMapping("/{participantId}/rejeitar")
    @Operation(summary = "Rejeitar inscrição")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscrição rejeitada"),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado")
    })
    public ResponseEntity<ParticipantResponseDTO> rejeitar(
            @PathVariable Long eventId,
            @PathVariable Long participantId
    ) {
        return ResponseEntity.ok(toDTO(service.rejeitarInscricao(eventId, participantId)));
    }

    // =========================================================
    // MARCAR PRESENÇA
    // =========================================================

    @PatchMapping("/{participantId}/presenca")
    @Operation(summary = "Marcar presença")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presença registrada"),
            @ApiResponse(responseCode = "400", description = "Participante não aprovado"),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado")
    })
    public ResponseEntity<ParticipantResponseDTO> marcarPresenca(
            @PathVariable Long eventId,
            @PathVariable Long participantId,
            @RequestBody @Valid PresencaRequestDTO dto
    ) {
        return ResponseEntity.ok(
                toDTO(service.marcarPresenca(eventId, participantId, dto.getPresenca()))
        );
    }

    // =========================================================
    // REMOVER PARTICIPANTE
    // =========================================================

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

    // =========================================================
    // CHECAR EMAIL
    // =========================================================

    @GetMapping("/check-email")
    @Operation(summary = "Checar se e-mail já está inscrito")
    public ResponseEntity<Map<String, Boolean>> checkEmailRegistered(
            @PathVariable Long eventId,
            @RequestParam String email
    ) {
        return ResponseEntity.ok(
                Map.of("emailInscrito", service.isEmailRegistered(eventId, email))
        );
    }

    // =========================================================
    // HELPER — converte entidade para DTO
    // =========================================================

    private ParticipantResponseDTO toDTO(Participant p) {
        return new ParticipantResponseDTO(
                p.getId(),
                p.getName(),
                p.getEmail(),
                p.getPhone(),
                p.getCpf(),
                p.getStatus(),
                p.getDataInscricao(),
                p.getPresenca()
        );
    }
}