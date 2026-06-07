package com.grupo1.backGrupo1.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventSpeakerDTO {
    private Long id;
    private String name;
    private String bio;
    private List<String> topics = new ArrayList<>();
    private String agenda;
}
