package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.service.EventsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventsService service;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new EventsController(service))
                .build();
    }

    @Test
    void getById_returnsOnlyActiveParticipants() throws Exception {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Evento");
        event.setMaxParticipants(10);
        event.setCategory("Tecnologia");

        Participant active = new Participant();
        active.setId(10L);
        active.setName("Ativo");
        active.setEmail("ativo@example.com");
        active.setDeleted(false);
        active.setEvent(event);

        Participant deleted = new Participant();
        deleted.setId(11L);
        deleted.setName("Cancelado");
        deleted.setEmail("cancelado@example.com");
        deleted.setDeleted(true);
        deleted.setEvent(event);

        event.getParticipants().add(active);
        event.getParticipants().add(deleted);

        when(service.getById(1L)).thenReturn(event);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants", hasSize(1)))
                .andExpect(jsonPath("$.participants[0].id").value(10L))
                .andExpect(jsonPath("$.participants[0].email").value("ativo@example.com"));
    }
}
