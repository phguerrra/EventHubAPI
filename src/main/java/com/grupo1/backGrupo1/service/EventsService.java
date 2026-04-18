package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventsService {

    private final EventsRepository repository;
    private final ParticipantRepository participantRepository;

    public EventsService(EventsRepository repository, ParticipantRepository participantRepository) {
        this.repository = repository;
        this.participantRepository = participantRepository;
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
        repository.deleteById(id);
    }

    public void cancelRegistration(Long eventId, Long participantId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        event.getParticipants().remove(participant);
        participant.setEvento(null);

        repository.save(event);
    }
}