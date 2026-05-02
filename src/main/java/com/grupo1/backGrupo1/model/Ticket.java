package com.grupo1.backGrupo1.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Data;

@Data
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ticketId;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private boolean usado = false;

    @Column(nullable = false)
    private Instant dataCriacao;
}
