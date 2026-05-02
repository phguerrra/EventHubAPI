package com.grupo1.backGrupo1.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class CreateTicketRequestDTO {
    @NotNull
    private Long eventId;

    @Min(1)
    private Long expirationMinutes = 60L;
}
