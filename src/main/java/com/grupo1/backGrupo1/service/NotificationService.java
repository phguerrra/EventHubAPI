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

    public NotificationService(NotificationRepository notificationRepository,
                        EventsRepository eventsRepository) {
        this.notificationRepository = notificationRepository;
        this.eventsRepository = eventsRepository;
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

        Notification aviso = new Notification();
        aviso.setTitulo(dto.getTitulo());
        aviso.setConteudo(dto.getConteudo());
        aviso.setTipo(dto.getType());
        aviso.setEventId(dto.getEventId());

        return NotificationResponseDto.from(notificationRepository.save(aviso));
    }

    public void delete(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aviso não encontrado com id: " + id));

        if (notification.isDeleted()) {
            throw new EntityNotFoundException("Aviso não encontrado com id: " + id);
        }

        notification.setDeleted(true);
        notificationRepository.save(notification);
    }
}
