package com.grupo1.backGrupo1.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ValidacaoRequestDTO {
    @NotBlank
    private String token;
}
