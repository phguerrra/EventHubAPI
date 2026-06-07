package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.SpeakerDTO;
import com.grupo1.backGrupo1.dto.SpeakerScheduleDTO;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Speaker;
import com.grupo1.backGrupo1.model.SpeakerSchedule;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.SpeakerRepository;
import com.grupo1.backGrupo1.repository.SpeakerScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeakerServiceTest {

    @Mock SpeakerRepository speakerRepository;
    @Mock SpeakerScheduleRepository scheduleRepository;
    @Mock EventsRepository eventsRepository;

    @InjectMocks SpeakerService service;

    private Speaker speaker;
    private Event event;

    @BeforeEach
    void setUp() {
        speaker = new Speaker();
        speaker.setId(1L);
        speaker.setName("João Palestrante");
        speaker.setEmail("joao@speaker.com");
        speaker.setBio("Especialista em Java");
        speaker.setDeleted(false);

        event = new Event();
        event.setId(1L);
        event.setTitle("Evento Java");
        event.setDate(LocalDate.now().plusDays(5));
        event.setMaxParticipants(100);
        event.setCategory("Tecnologia");
        event.setDeleted(false);
    }

    @Test
    @DisplayName("Deve criar speaker com sucesso")
    void create_validDto_success() {
        SpeakerDTO dto = new SpeakerDTO();
        dto.setName("João Palestrante");
        dto.setEmail("joao@speaker.com");
        dto.setBio("Bio do palestrante");
        dto.setTopics(List.of("Java", "Spring"));

        when(speakerRepository.save(any())).thenReturn(speaker);

        Speaker result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("João Palestrante");
        verify(speakerRepository).save(any(Speaker.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar speaker inexistente")
    void getById_notFound_throwsException() {
        when(speakerRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve adicionar schedule com horários válidos")
    void addSchedule_validTimes_success() {
        SpeakerScheduleDTO dto = new SpeakerScheduleDTO();
        dto.setEventId(1L);
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(15, 30));
        dto.setDescription("Palestra sobre Spring Boot");

        SpeakerSchedule schedule = new SpeakerSchedule();
        schedule.setId(1L);
        schedule.setSpeaker(speaker);
        schedule.setEvent(event);
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());

        when(speakerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(speaker));
        when(eventsRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(event));
        when(scheduleRepository.save(any())).thenReturn(schedule);

        SpeakerSchedule result = service.addSchedule(1L, dto);

        assertThat(result.getStartTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(result.getEndTime()).isEqualTo(LocalTime.of(15, 30));
    }

    @Test
    @DisplayName("Deve lançar exceção quando endTime <= startTime")
    void addSchedule_invalidTimes_throwsException() {
        SpeakerScheduleDTO dto = new SpeakerScheduleDTO();
        dto.setEventId(1L);
        dto.setStartTime(LocalTime.of(15, 0));
        dto.setEndTime(LocalTime.of(14, 0)); // inválido

        when(speakerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(speaker));
        when(eventsRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> service.addSchedule(1L, dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("End time must be after start time");

        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando endTime igual ao startTime")
    void addSchedule_equalTimes_throwsException() {
        SpeakerScheduleDTO dto = new SpeakerScheduleDTO();
        dto.setEventId(1L);
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(14, 0)); // igual

        when(speakerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(speaker));
        when(eventsRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> service.addSchedule(1L, dto))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção quando schedule não pertence ao speaker")
    void removeSchedule_wrongSpeaker_throwsException() {
        Speaker otherSpeaker = new Speaker();
        otherSpeaker.setId(2L);

        SpeakerSchedule schedule = new SpeakerSchedule();
        schedule.setId(1L);
        schedule.setSpeaker(otherSpeaker); // pertence a outro speaker

        when(speakerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(speaker));
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        assertThatThrownBy(() -> service.removeSchedule(1L, 1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    @DisplayName("Soft delete deve marcar speaker como deletado")
    void delete_existingSpeaker_marksAsDeleted() {
        when(speakerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(speaker));
        when(speakerRepository.save(any())).thenReturn(speaker);

        service.delete(1L);

        assertThat(speaker.isDeleted()).isTrue();
        verify(speakerRepository).save(speaker);
    }

    @Test
    @DisplayName("Deve listar apenas speakers não deletados")
    void listAll_returnsOnlyActive() {
        when(speakerRepository.findByDeletedFalse()).thenReturn(List.of(speaker));

        List<Speaker> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isDeleted()).isFalse();
    }
}