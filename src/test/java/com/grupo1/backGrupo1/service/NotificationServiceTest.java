package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.NotificationDto;
import com.grupo1.backGrupo1.dto.NotificationResponseDto;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Notification;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock NotificationRepository notificationRepository;
    @Mock EventsRepository eventsRepository;
    @Mock NotificationSseService sseService;

    @InjectMocks NotificationService service;

    private Event event;
    private Notification notification;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setTitle("Evento Teste");
        event.setDate(LocalDate.now().plusDays(10));
        event.setMaxParticipants(50);
        event.setCategory("Tecnologia");
        event.setDeleted(false);

        notification = new Notification();
        notification.setId(1L);
        notification.setTitulo("Aviso Teste");
        notification.setConteudo("Conteúdo do aviso");
        notification.setTipo(Notification.Type.GENERAL);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setDeleted(false);
    }

    @Test
    @DisplayName("Deve criar aviso geral com sucesso")
    void create_generalNotification_success() {
        NotificationDto dto = new NotificationDto();
        dto.setTitulo("Aviso Geral");
        dto.setConteudo("Conteúdo");
        dto.setType(Notification.Type.GENERAL);

        when(notificationRepository.save(any())).thenReturn(notification);

        NotificationResponseDto result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getTitulo()).isEqualTo("Aviso Teste");
        verify(notificationRepository).save(any(Notification.class));
        verify(sseService).publish(eq("notification-created"), any());
    }

    @Test
    @DisplayName("Deve criar aviso de evento específico com sucesso")
    void create_specificEventNotification_success() {
        NotificationDto dto = new NotificationDto();
        dto.setTitulo("Aviso Evento");
        dto.setConteudo("Conteúdo");
        dto.setType(Notification.Type.SPECIFIC_EVENT);
        dto.setEventId(1L);

        notification.setTipo(Notification.Type.SPECIFIC_EVENT);
        notification.setEventId(1L);

        when(eventsRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(event));
        when(notificationRepository.save(any())).thenReturn(notification);

        NotificationResponseDto result = service.create(dto);

        assertThat(result.getEventId()).isEqualTo(1L);
        verify(eventsRepository).findByIdAndDeletedFalse(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando SPECIFIC_EVENT sem eventId")
    void create_specificEventWithoutEventId_throwsException() {
        NotificationDto dto = new NotificationDto();
        dto.setTitulo("Aviso");
        dto.setConteudo("Conteúdo");
        dto.setType(Notification.Type.SPECIFIC_EVENT);
        dto.setEventId(null);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("eventId é obrigatório");

        verify(notificationRepository, never()).save(any());
        verify(sseService, never()).publish(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando evento não existe")
    void create_specificEventNotFound_throwsException() {
        NotificationDto dto = new NotificationDto();
        dto.setTitulo("Aviso");
        dto.setConteudo("Conteúdo");
        dto.setType(Notification.Type.SPECIFIC_EVENT);
        dto.setEventId(99L);

        when(eventsRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar aviso com sucesso")
    void delete_existingNotification_success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenReturn(notification);

        service.delete(1L);

        assertThat(notification.isDeleted()).isTrue();
        verify(notificationRepository).save(notification);
        verify(sseService).publish(eq("notification-deleted"), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar aviso já deletado")
    void delete_alreadyDeleted_throwsException() {
        notification.setDeleted(true);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar todos os avisos não deletados")
    void listAll_returnsOnlyNotDeleted() {
        when(notificationRepository.findByDeletedFalseOrderByCreatedAtDesc())
                .thenReturn(List.of(notification));

        List<NotificationResponseDto> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitulo()).isEqualTo("Aviso Teste");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar avisos de evento inexistente")
    void listByEvento_eventNotFound_throwsException() {
        when(eventsRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listByEvento(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}