package com.grupo1.backGrupo1.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    void shouldCreateEventWithDefaultValues() {
        Event e = new Event();
        e.setTitle("Festa");
        e.setDescription("Descrição");
        e.setDate(LocalDate.of(2026,1,1));
        e.setTime(LocalTime.of(20,0));
        e.setLocation("Praça");
        e.setMaxParticipants(100);
        e.setMajority18(true);
        e.setCategory("Música");

        assertNotNull(e.getParticipants(), "Participants list should be initialized");
        assertEquals(0, e.getParticipants().size());
        assertFalse(e.isDeleted(), "Deleted should default to false");
        assertEquals("Festa", e.getTitle());
        assertEquals("Música", e.getCategory());
    }

    @Test
    void shouldAddParticipantToEvent() {
        Event e = new Event();
        Participant p = new Participant();
        p.setName("João");
        p.setEmail("joao@example.com");

        p.setEvent(e);
        e.getParticipants().add(p);

        assertEquals(1, e.getParticipants().size());
        assertSame(e, e.getParticipants().get(0).getEvent());
        assertEquals("joao@example.com", e.getParticipants().get(0).getEmail());
    }
}
