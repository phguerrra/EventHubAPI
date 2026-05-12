package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.ParticipantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventsServiceTest {

    @Mock
    private EventsRepository repository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private EventsService service;

    public EventsServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveEventSuccessfully() {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setMaxParticipants(10);
        event.setDate(LocalDate.now());
        event.setCategory("Tecnologia"); // importante

        when(repository.save(any(Event.class))).thenReturn(event);

        Event result = service.saveEvent(event);

        assertNotNull(result);
        assertEquals("Test Event", result.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        Event event = new Event();
        event.setMaxParticipants(10);
        event.setDate(LocalDate.now());
        event.setCategory("Tecnologia");

        assertThrows(RuntimeException.class, () -> {
            service.saveEvent(event);
        });
    }

    @Test
    void shouldThrowExceptionWhenMaxParticipantsIsInvalid() {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setMaxParticipants(0);
        event.setDate(LocalDate.now());
        event.setCategory("Tecnologia");

        assertThrows(RuntimeException.class, () -> {
            service.saveEvent(event);
        });
    }

    @Test
    void shouldThrowExceptionWhenDateIsNull() {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setMaxParticipants(10);
        event.setCategory("Tecnologia");

        assertThrows(RuntimeException.class, () -> {
            service.saveEvent(event);
        });
    }

    @Test
    void shouldPerformSoftDelete() {
        Event event = new Event();
        event.setDeleted(false);

        when(repository.findByIdAndDeletedFalse(1L))
                .thenReturn(java.util.Optional.of(event));

        service.deleteById(1L);

        assertTrue(event.isDeleted());
        verify(repository, times(1)).save(event);
    }
}