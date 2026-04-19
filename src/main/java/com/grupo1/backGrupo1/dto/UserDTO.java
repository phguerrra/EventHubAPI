package com.grupo1.backGrupo1.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class UserDTO {

    private String name;
    private String email;
    private String password;
    private String cpf;
    private LocalDate dataNascimento;


}