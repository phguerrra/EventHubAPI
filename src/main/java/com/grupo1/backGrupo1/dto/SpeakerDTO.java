package com.grupo1.backGrupo1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class SpeakerDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String email;

    private String bio;

    private List<String> topics;
}