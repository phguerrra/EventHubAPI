package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.NotificationResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationSseService {

    private static final long TIMEOUT_MS = 30L * 60L * 1000L;

    private record Client(SseEmitter emitter, Long eventId) {
    }

    private final List<Client> clients = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe(Long eventId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        Client client = new Client(emitter, eventId);
        clients.add(client);

        emitter.onCompletion(() -> clients.remove(client));
        emitter.onTimeout(() -> {
            clients.remove(client);
            emitter.complete();
        });
        emitter.onError((error) -> clients.remove(client));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("connected"));
        } catch (IOException e) {
            clients.remove(client);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void publish(String eventName, NotificationResponseDto notification) {
        for (Client client : clients) {
            if (!shouldReceive(client.eventId(), notification)) continue;

            try {
                client.emitter().send(SseEmitter.event()
                        .name(eventName)
                        .id(String.valueOf(notification.getId()))
                        .data(notification));
            } catch (IOException e) {
                clients.remove(client);
                client.emitter().completeWithError(e);
            }
        }
    }

    private boolean shouldReceive(Long subscribedEventId, NotificationResponseDto notification) {
        if (subscribedEventId == null) return true;
        if (notification.getEventId() == null) return true;
        return subscribedEventId.equals(notification.getEventId());
    }
}
