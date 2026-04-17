package com.grupo1.backGrupo1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventDTO {

    @NotBlank(message = "Título do evento é obrigatório")
    private String title;

    private String description;

    @NotNull(message = "Data do evento é obrigatória")
    private LocalDate date;

    private LocalTime time;

    private String location;

    @NotNull(message = "O número máximo de participantes é obrigatório")
    @Min(value = 1, message = "O número máximo de participantes deve ser maior que zero")
    private Integer maxParticipants;

    private Boolean majority18 = false;


}
