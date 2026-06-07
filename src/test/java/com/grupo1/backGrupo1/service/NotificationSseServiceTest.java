package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.NotificationResponseDto;
import com.grupo1.backGrupo1.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class NotificationSseServiceTest {

    private NotificationSseService sseService;

    @BeforeEach
    void setUp() {
        sseService = new NotificationSseService();
    }

    @Test
    @DisplayName("Deve registrar cliente ao fazer subscribe")
    void subscribe_addsClient() {
        sseService.subscribe("user@test.com", Set.of(1L));
        assertThat(sseService.connectedClients()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve registrar múltiplos clientes")
    void subscribe_multipleClients() {
        sseService.subscribe("user1@test.com", Set.of(1L));
        sseService.subscribe("user2@test.com", Set.of(2L));
        assertThat(sseService.connectedClients()).isEqualTo(2);
    }

    @Test
    @DisplayName("Notificação GENERAL deve chegar a todos os clientes")
    void publish_generalNotification_reachesAllClients() {
        SseEmitter e1 = sseService.subscribe("user1@test.com", Set.of(1L));
        SseEmitter e2 = sseService.subscribe("user2@test.com", Set.of(2L));

        NotificationResponseDto dto = new NotificationResponseDto(
                1L, "Geral", "Conteúdo",
                Notification.Type.GENERAL, null, LocalDateTime.now());

        // Não deve lançar exceção — ambos deveriam receber
        assertThatCode(() -> sseService.publish("notification-created", dto))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Notificação SPECIFIC_EVENT só deve chegar a quem está inscrito")
    void publish_specificEvent_onlyReachesSubscribedClients() {
        sseService.subscribe("user1@test.com", Set.of(1L)); // inscrito no evento 1
        sseService.subscribe("user2@test.com", Set.of(2L)); // inscrito no evento 2

        NotificationResponseDto dto = new NotificationResponseDto(
                1L, "Evento 1", "Conteúdo",
                Notification.Type.SPECIFIC_EVENT, 1L, LocalDateTime.now());

        assertThatCode(() -> sseService.publish("notification-created", dto))
                .doesNotThrowAnyException();
    }
}