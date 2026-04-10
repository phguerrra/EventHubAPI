package com.Grupo1.BackGrupo1.controller;

import com.Grupo1.BackGrupo1.model.Events;
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
    public List<Events> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Events buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public Events criar(@RequestBody Events evento) {
        return service.salvar(evento);
    }

    @PutMapping("/{id}")
    public Events atualizar(@PathVariable Long id, @RequestBody Events evento) {
        evento.setId(id);
        return service.salvar(evento);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}