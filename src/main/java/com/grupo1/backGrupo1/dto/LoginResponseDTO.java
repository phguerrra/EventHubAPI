package com.grupo1.backGrupo1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String message;
    private String name;
    private String email;
    private String role;
    private Boolean isAdmin;
    private String token;
    private String refreshToken;

    // Convenience constructor used by service.login(...) which returns name,email,role,token,refreshToken
    public LoginResponseDTO(String name, String email, String role, String token, String refreshToken) {
        this.message = null;
        this.name = name;
        this.email = email;
        this.role = role;
        this.isAdmin = false;
        this.token = token;
        this.refreshToken = refreshToken;
    }

    // Convenience constructor used by controller when building a response without refreshToken
    public LoginResponseDTO(String message, String name, String email, String role, Boolean isAdmin, String token) {
        this.message = message;
        this.name = name;
        this.email = email;
        this.role = role;
        this.isAdmin = isAdmin;
        this.token = token;
        this.refreshToken = null;
    }

}
