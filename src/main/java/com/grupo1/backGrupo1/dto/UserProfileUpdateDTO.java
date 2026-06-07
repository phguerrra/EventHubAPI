package com.grupo1.backGrupo1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileUpdateDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String phone;

    private String address;

    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;
}
