package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.NotificationResponseDto;
import com.grupo1.backGrupo1.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationSseService {

    private static final Logger log = LoggerFactory.getLogger(NotificationSseService.class);
    private static final long TIMEOUT_MS = 30L * 60L * 1000L;

    // Representa um cliente conectado via SSE
    private record Client(
            SseEmitter emitter,
            String userEmail,         // email do usuário logado
            Set<Long> subscribedEventIds // IDs dos eventos em que está inscrito
    ) {}

    private final List<Client> clients = new CopyOnWriteArrayList<>();

    /**
     * Abre um canal SSE para o usuário.
     * subscribedEventIds = conjunto dos IDs dos eventos em que o usuário está inscrito.
     */
    public SseEmitter subscribe(String userEmail, Set<Long> subscribedEventIds) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        Client client = new Client(emitter, userEmail, subscribedEventIds);
        clients.add(client);

        emitter.onCompletion(() -> {
            clients.remove(client);
            log.debug("SSE desconectado: {}", userEmail);
        });
        emitter.onTimeout(() -> {
            clients.remove(client);
            emitter.complete();
            log.debug("SSE timeout: {}", userEmail);
        });
        emitter.onError(e -> {
            clients.remove(client);
            log.debug("SSE erro: {} | {}", userEmail, e.getMessage());
        });

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Conectado como " + userEmail));
        } catch (IOException e) {
            clients.remove(client);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * Publica uma notificação para todos os clientes que devem recebê-la.
     * Regras:
     * - Notificação GENERAL → todos os usuários recebem
     * - Notificação SPECIFIC_EVENT → só quem está inscrito no evento recebe
     */
    public void publish(String eventName, NotificationResponseDto notification) {
        List<Client> snapshot = List.copyOf(clients); // cópia para evitar ConcurrentModificationException

        for (Client client : snapshot) {
            if (!shouldReceive(client, notification)) continue;

            try {
                client.emitter().send(SseEmitter.event()
                        .name(eventName)
                        .id(String.valueOf(notification.getId()))
                        .data(notification));
            } catch (IOException e) {
                clients.remove(client);
                client.emitter().completeWithError(e);
                log.warn("Falha ao enviar SSE para {}: {}", client.userEmail(), e.getMessage());
            }
        }
    }

    private boolean shouldReceive(Client client, NotificationResponseDto notification) {
        // Notificação geral — todos recebem
        if (notification.getType() == Notification.Type.GENERAL) {
            return true;
        }

        // Notificação de evento específico — só quem está inscrito no evento
        if (notification.getEventId() == null) return false;
        return client.subscribedEventIds().contains(notification.getEventId());
    }

    public int connectedClients() {
        return clients.size();
    }
}