package com.grupo1.backGrupo1.dto;

import com.grupo1.backGrupo1.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private String titulo;
    private String conteudo;
    private Notification.Type type;
    private Long eventId;
    private LocalDateTime createdAt;

    public static NotificationResponseDto from(Notification a) {
        return new NotificationResponseDto(
                a.getId(),
                a.getTitulo(),
                a.getConteudo(),
                a.getTipo(),
                a.getEventId(),
                a.getCreatedAt()
        );
    }
}
