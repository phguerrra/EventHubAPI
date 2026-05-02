package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.TicketResponseDTO;
import com.grupo1.backGrupo1.exception.TicketAlreadyUsedException;
import com.grupo1.backGrupo1.exception.TicketNotFoundException;
import com.grupo1.backGrupo1.model.Ticket;
import com.grupo1.backGrupo1.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketJwtService jwtService;
    private final QrCodeService qrCodeService;

    public TicketService(TicketRepository ticketRepository, TicketJwtService jwtService, QrCodeService qrCodeService) {
        this.ticketRepository = ticketRepository;
        this.jwtService = jwtService;
        this.qrCodeService = qrCodeService;
    }

    @Transactional
    public TicketResponseDTO createTicket(Long eventId, long expirationMinutes) {
        String ticketUuid = UUID.randomUUID().toString();
        long expirationMillis = expirationMinutes * 60 * 1000;
        String token = jwtService.generateToken(ticketUuid, eventId, expirationMillis);

        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketUuid);
        ticket.setEventId(eventId);
        ticket.setUsado(false);
        ticket.setDataCriacao(Instant.now());
        ticketRepository.save(ticket);

        String qrBase64 = qrCodeService.generateQrPngBase64(token, 300);
        return new TicketResponseDTO(ticketUuid, eventId, token, qrBase64);
    }

    @Transactional
    public void validateAndUseTicket(String token) {
        java.util.Map<String, Object> payload = jwtService.validateTokenAndGetPayload(token);
        String ticketId = payload.get("sub").toString();

        Ticket t = ticketRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket não encontrado"));

        if (t.isUsado()) {
            throw new TicketAlreadyUsedException("Ticket já utilizado");
        }

        t.setUsado(true);
        ticketRepository.save(t);
    }
}
