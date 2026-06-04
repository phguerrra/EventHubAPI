package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.NotificationDto;
import com.grupo1.backGrupo1.dto.NotificationResponseDto;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.Notification;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EventsRepository eventsRepository;
    private final NotificationSseService sseService;

    public NotificationService(NotificationRepository notificationRepository,
                               EventsRepository eventsRepository,
                               NotificationSseService sseService) {
        this.notificationRepository = notificationRepository;
        this.eventsRepository = eventsRepository;
        this.sseService = sseService;
    }

    public List<NotificationResponseDto> listAll() {
        return notificationRepository.findByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDto> listGerais() {
        return notificationRepository
                .findByDeletedFalseAndTipoOrderByCreatedAtDesc(Notification.Type.GENERAL)
                .stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDto> listByEvento(Long eventId) {
        // Valida se evento existe
        eventsRepository.findByIdAndDeletedFalse(eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Evento não encontrado com id: " + eventId));

        return notificationRepository
                .findByDeletedFalseAndEventIdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }

    public NotificationResponseDto create(NotificationDto dto) {
        if (dto.getType() == Notification.Type.SPECIFIC_EVENT) {
            if (dto.getEventId() == null) {
                throw new BusinessRuleException(
                        "eventId é obrigatório para avisos de evento específico");
            }
            eventsRepository.findByIdAndDeletedFalse(dto.getEventId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Evento não encontrado com id: " + dto.getEventId()));
        }

        Notification notification = new Notification();
        notification.setTitulo(dto.getTitulo());
        notification.setConteudo(dto.getConteudo());
        notification.setTipo(dto.getType());
        notification.setEventId(dto.getType() == Notification.Type.SPECIFIC_EVENT ? dto.getEventId() : null);

        NotificationResponseDto response = NotificationResponseDto.from(notificationRepository.save(notification));
        sseService.publish("notification-created", response);
        return response;
    }

    public NotificationResponseDto update(Long id, NotificationDto dto) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aviso não encontrado com id: " + id));

        if (notification.isDeleted()) {
            throw new EntityNotFoundException("Aviso não encontrado com id: " + id);
        }

        if (dto.getType() == Notification.Type.SPECIFIC_EVENT) {
            if (dto.getEventId() == null) {
                throw new BusinessRuleException(
                        "eventId é obrigatório para avisos de evento específico");
            }
            eventsRepository.findByIdAndDeletedFalse(dto.getEventId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Evento não encontrado com id: " + dto.getEventId()));
        }

        notification.setTitulo(dto.getTitulo());
        notification.setConteudo(dto.getConteudo());
        notification.setTipo(dto.getType());
        notification.setEventId(dto.getType() == Notification.Type.SPECIFIC_EVENT ? dto.getEventId() : null);

        NotificationResponseDto response = NotificationResponseDto.from(notificationRepository.save(notification));
        sseService.publish("notification-updated", response);
        return response;
    }

    public void delete(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aviso não encontrado com id: " + id));

        if (notification.isDeleted()) {
            throw new EntityNotFoundException("Aviso não encontrado com id: " + id);
        }

        notification.setDeleted(true);
        NotificationResponseDto response = NotificationResponseDto.from(notificationRepository.save(notification));
        sseService.publish("notification-deleted", response);
    }
}
