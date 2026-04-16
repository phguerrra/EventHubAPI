package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.service.EventsService;
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
}