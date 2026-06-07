package com.grupo1.backGrupo1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class EventSpeaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_speaker_topics", joinColumns = @JoinColumn(name = "speaker_id"))
    @Column(name = "topic")
    private List<String> topics = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String agenda;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
