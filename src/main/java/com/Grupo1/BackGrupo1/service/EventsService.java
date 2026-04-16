package com.Grupo1.BackGrupo1.service;

import com.Grupo1.BackGrupo1.model.Event;
import com.Grupo1.BackGrupo1.model.Participant;
import com.Grupo1.BackGrupo1.repository.EventsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventsService {

    private final EventsRepository repository;

    public EventsService(EventsRepository repository) {
        this.repository = repository;
    }

    public List<Event> listar() {
        return repository.findAll();
    }

    public Event buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    }

    public Event salvar(Event evento) {
        return repository.save(evento);
    }

    public void deletar(Long id) {
        Event event = buscar(id);
        repository.delete(event);
    }

    public Participant cadastrarParticipante(Long eventId, Participant participant) {
        Event event = buscar(eventId);

        if (event.getParticipants().size() >= event.getMaxParticipants()) {
            throw new RuntimeException("Limite de participantes atingido");
        }

        participant.setEvent(event);
        event.getParticipants().add(participant);

        repository.save(event);

        return participant;
    }

    public List<Participant> listarParticipantes(Long eventId) {
        Event event = buscar(eventId);
        return event.getParticipants();
    }
}