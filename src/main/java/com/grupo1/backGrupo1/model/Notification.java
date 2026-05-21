package com.grupo1.backGrupo1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification")
public class Notification {

    public enum Type{
        GENERAL, SPECIFIC_EVENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type tipo = Type.GENERAL;

    // Nullable — só preenchido quando tipo = EVENTO_ESPECIFICO
    private Long eventId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean deleted = false;
}
