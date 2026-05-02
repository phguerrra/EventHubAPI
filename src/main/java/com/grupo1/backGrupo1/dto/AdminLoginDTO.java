package com.grupo1.backGrupo1.dto;

import lombok.Data;

@Data
public class AdminLoginDTO {
    private String username;
    private String password;
    private Long expirationMinutes = 60L;
}
