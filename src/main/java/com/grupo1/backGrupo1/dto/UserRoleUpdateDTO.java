package com.grupo1.backGrupo1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRoleUpdateDTO {
    @NotBlank
    private String role;
}
