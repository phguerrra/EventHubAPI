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

    // ✅ TESTE DE SUCESSO
    @Test
    void deveSalvarEventoComSucesso() {
        Event event = new Event();
        event.setTitle("Evento Teste");
        event.setMaxParticipants(10);
        event.setDate(LocalDate.now());

        when(repository.save(any(Event.class))).thenReturn(event);

        Event resultado = service.saveEvent(event);

        assertNotNull(resultado);
        assertEquals("Evento Teste", resultado.getTitle());
    }

    // ❌ TESTE DE ERRO (TÍTULO NULO)
    @Test
    void deveDarErroQuandoTituloForNulo() {
        Event event = new Event();
        event.setMaxParticipants(10);
        event.setDate(LocalDate.now());

        assertThrows(RuntimeException.class, () -> {
            service.saveEvent(event);
        });
    }

    // ❌ TESTE DE ERRO (MAX PARTICIPANTS INVÁLIDO)
    @Test
    void deveDarErroQuandoMaxParticipantsForInvalido() {
        Event event = new Event();
        event.setTitle("Evento Teste");
        event.setMaxParticipants(0);
        event.setDate(LocalDate.now());

        assertThrows(RuntimeException.class, () -> {
            service.saveEvent(event);
        });
    }

    // ❌ TESTE DE ERRO (DATA NULA)
    @Test
    void deveDarErroQuandoDataForNula() {
        Event event = new Event();
        event.setTitle("Evento Teste");
        event.setMaxParticipants(10);

        assertThrows(RuntimeException.class, () -> {
            service.saveEvent(event);
        });
    }

    // 🧨 TESTE DE SOFT DELETE
    @Test
    void deveFazerSoftDelete() {
        Event event = new Event();
        event.setDeleted(false);

        when(repository.findByIdAndDeletedFalse(1L))
                .thenReturn(java.util.Optional.of(event));

        service.deleteById(1L);

        assertTrue(event.isDeleted());
        verify(repository, times(1)).save(event);
    }
}