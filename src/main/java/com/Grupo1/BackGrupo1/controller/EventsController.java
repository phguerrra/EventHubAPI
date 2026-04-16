package com.Grupo1.BackGrupo1.controller;

import com.Grupo1.BackGrupo1.model.Event;
import com.Grupo1.BackGrupo1.model.Participant;
import com.Grupo1.BackGrupo1.service.EventsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventsController {

    private final EventsService service;

    public EventsController(EventsService service) {
        this.service = service;
    }

    @GetMapping
    public List<Event> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Event buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public Event criar(@RequestBody Event evento) {
        return service.salvar(evento);
    }

    @PutMapping("/{id}")
    public Event atualizar(@PathVariable Long id, @RequestBody Event evento) {
        evento.setId(id);
        return service.salvar(evento);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @PostMapping("/{id}/participants")
    public Participant cadastrarParticipante(@PathVariable Long id, @RequestBody Participant participant) {
        return service.cadastrarParticipante(id, participant);
    }

    @GetMapping("/{id}/participants")
    public List<Participant> listarParticipantes(@PathVariable Long id) {
        return service.listarParticipantes(id);

    }
}
