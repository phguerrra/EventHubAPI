package com.grupo1.backGrupo1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

import java.time.LocalDateTime;

@Data
@Entity

public class EventMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String Description;
    private String fileUrl;
    private String fileType;
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

}
