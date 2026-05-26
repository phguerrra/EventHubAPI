package com.grupo1.backGrupo1.dto;

import com.grupo1.backGrupo1.model.Participant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String cpf;
    private Participant.Status status;
    private LocalDateTime dataInscricao;
    private Participant.Presenca presenca;
}