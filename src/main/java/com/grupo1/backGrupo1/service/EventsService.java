package com.grupo1.backGrupo1.service;

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
}