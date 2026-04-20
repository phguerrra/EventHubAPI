package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
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

    public List<Event> listAll() {
        return repository.findAll();
    }

    public Event getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado com id: " + id));
    }

    public Event saveEvent(Event event) {
        if (event == null) {
            throw new BusinessRuleException("Evento não pode ser nulo");
        }
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new BusinessRuleException("Título do evento é obrigatório");
        }
        if (event.getMaxParticipants() <= 0) {
            throw new BusinessRuleException("O número máximo de participantes deve ser maior que zero");
        }
        if (event.getDate() == null) {
            throw new BusinessRuleException("Data do evento é obrigatória");
        }
        if (event.getParticipants() != null && event.getParticipants().size() > event.getMaxParticipants()) {
            throw new BusinessRuleException("Número de participantes excede o máximo permitido");
        }
        return repository.save(event);
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Evento não encontrado com id: " + id);
        }
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