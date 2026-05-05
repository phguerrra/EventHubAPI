package com.grupo1.backGrupo1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String message;
    private Long userId;
    private String name;
    private String email;
    private String role;
    private Boolean isAdmin;
    private String token;
    private String refreshToken;
}