package com.Grupo1.BackGrupo1.service;

import com.Grupo1.BackGrupo1.model.Events;
import com.Grupo1.BackGrupo1.repository.EventsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventsService {

    private final EventsRepository repository;

    public EventsService(EventsRepository repository) {
        this.repository = repository;
    }

    public List<Events> listar() {
        return repository.findAll();
    }

    public Events buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    }

    public Events salvar(Events evento) {
        return repository.save(evento);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}