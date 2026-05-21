package com.grupo1.backGrupo1.dto;

import com.grupo1.backGrupo1.model.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationDto {
    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotBlank(message = "Conteúdo é obrigatório")
    private String conteudo;

    @NotNull(message = "Tipo é obrigatório")
    private Notification.Type type;

    // Obrigatório apenas quando tipo = EVENTO_ESPECIFICO
    private Long eventId;
}
