package com.grupo1.backGrupo1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private int maxParticipants;
    private boolean majority18;
    private String category;
    private boolean requiresApproval;
    private String imageUrl;
    private List<ParticipantResponseDTO> participants;
}