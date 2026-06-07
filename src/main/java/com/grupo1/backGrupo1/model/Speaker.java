package com.grupo1.backGrupo1.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "speaker")
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String photoUrl;

    @ElementCollection
    @CollectionTable(
            name = "speaker_topics",
            joinColumns = @JoinColumn(name = "speaker_id")
    )
    @Column(name = "topic")
    private List<String> topics = new ArrayList<>();

    @OneToMany(mappedBy = "speaker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpeakerSchedule> schedule = new ArrayList<>();

    private boolean deleted = false;
}