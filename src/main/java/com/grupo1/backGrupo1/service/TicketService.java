package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.TicketResponseDTO;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.exception.TicketAlreadyUsedException;
import com.grupo1.backGrupo1.exception.TicketNotFoundException;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.model.Ticket;
import com.grupo1.backGrupo1.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketJwtService jwtService;
    private final QrCodeService qrCodeService;
    private final ParticipantService participantService;

    public TicketService(
            TicketRepository ticketRepository,
            TicketJwtService jwtService,
            QrCodeService qrCodeService,
            ParticipantService participantService
    ) {
        this.ticketRepository = ticketRepository;
        this.jwtService = jwtService;
        this.qrCodeService = qrCodeService;
        this.participantService = participantService;
    }

    // =========================================================
    // CRIAR TICKET
    // Recebe o id do participante para vincular ao ingresso
    // =========================================================

    @Transactional
    public TicketResponseDTO createTicket(
            Long eventId,
            Long participantId,
            long expirationMinutes
    ) {
        String ticketUuid = UUID.randomUUID().toString();
        long expirationMillis = expirationMinutes * 60 * 1000;
        String token = jwtService.generateToken(ticketUuid, eventId, expirationMillis);

        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketUuid);
        ticket.setEventId(eventId);
        ticket.setParticipantId(participantId);
        ticket.setUsado(false);
        ticket.setDataCriacao(Instant.now());
        ticketRepository.save(ticket);

        String qrBase64 = qrCodeService.generateQrPngBase64(token, 300);
        return new TicketResponseDTO(ticketUuid, eventId, token, qrBase64);
    }

    @Transactional(readOnly = true)
    public TicketResponseDTO getTicketForParticipant(Long eventId, String email) {
        Participant participant = participantService.findParticipantByEventAndEmail(eventId, email);

        if (participant.getStatus() != Participant.Status.APROVADO) {
            throw new BusinessRuleException("Participante não aprovado");
        }

        Ticket ticket = ticketRepository.findByEventIdAndParticipantId(eventId, participant.getId())
                .orElseThrow(() -> new EntityNotFoundException("QR Code ainda não foi gerado"));

        long expirationMillis = 24L * 60L * 60L * 1000L;
        String token = jwtService.generateToken(ticket.getTicketId(), eventId, expirationMillis);
        String qrBase64 = qrCodeService.generateQrPngBase64(token, 300);

        return new TicketResponseDTO(ticket.getTicketId(), eventId, token, qrBase64);
    }

    // VALIDAR TICKET E MARCAR PRESENÇA
    // Quando o admin escaneia o QR, marca o ticket como usado
    // e atualiza a presença do participante para PRESENTE
    @Transactional
    public void validateAndUseTicket(String token) {
        Map<String, Object> payload = jwtService.validateTokenAndGetPayload(token);
        String ticketId = payload.get("sub").toString();

        Ticket ticket = ticketRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket não encontrado"));

        if (ticket.isUsado()) {
            throw new TicketAlreadyUsedException("Ticket já utilizado");
        }

        // Marca o ingresso como usado
        ticket.setUsado(true);
        ticketRepository.save(ticket);

        // Marca o participante como PRESENTE
        participantService.marcarPresencaPorId(ticket.getParticipantId());
    }
}
