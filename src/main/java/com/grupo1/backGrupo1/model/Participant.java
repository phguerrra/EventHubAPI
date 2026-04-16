package com.grupo1.backGrupo1.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"evento_id", "email"}))
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private com.grupo1.backGrupo1.model.Event evento;
}
