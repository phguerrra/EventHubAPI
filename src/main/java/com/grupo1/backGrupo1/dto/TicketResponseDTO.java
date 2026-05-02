package com.grupo1.backGrupo1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketResponseDTO {
    private String ticketId;
    private Long eventId;
    private String token;
    private String qrCodeBase64;
}
