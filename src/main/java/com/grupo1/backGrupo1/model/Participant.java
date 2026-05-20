package com.grupo1.backGrupo1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "email"}))
public class Participant {

    public enum Status {
        PENDENTE, APROVADO, REJEITADO
    }
    public enum Presenca {
        PENDENTE, PRESENTE, AUSENTE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String cpf;
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDENTE;

    private LocalDateTime dataInscricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Presenca presenca = Presenca.PENDENTE;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}