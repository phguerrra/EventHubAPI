package com.grupo1.backGrupo1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

@Data
public class SpeakerScheduleDTO {

    @NotNull(message = "eventId is required")
    private Long eventId;

    @NotNull(message = "startTime is required")
    private LocalTime startTime;

    @NotNull(message = "endTime is required")
    private LocalTime endTime;

    private String description;
}