package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.repository.EventsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventsService {

    private final EventsRepository repository;

    public EventsService(EventsRepository repository) {
        this.repository = repository;
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
}