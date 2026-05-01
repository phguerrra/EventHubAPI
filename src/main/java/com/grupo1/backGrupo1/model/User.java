package com.grupo1.backGrupo1.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    @Column(unique = true)
    private String cpf;

    private LocalDate dataNascimento;

    // New fields
    private String phone;
    private String address;
}