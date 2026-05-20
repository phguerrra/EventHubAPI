package com.grupo1.backGrupo1.dto;

import com.grupo1.backGrupo1.model.Participant;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PresencaRequestDTO {

    @NotNull(message = "Status de presença é obrigatório")
    private Participant.Presenca presenca;
}