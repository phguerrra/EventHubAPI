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
    public List<Event> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Event getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Event create(@org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid com.grupo1.backGrupo1.dto.EventDTO dto) {
        com.grupo1.backGrupo1.model.Event event = new com.grupo1.backGrupo1.model.Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setTime(dto.getTime());
        event.setLocation(dto.getLocation());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setMajority18(Boolean.TRUE.equals(dto.getMajority18()));
        return service.saveEvent(event);
    }

    @PutMapping("/{id}")
    public Event update(@PathVariable Long id, @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid com.grupo1.backGrupo1.dto.EventDTO dto) {
        com.grupo1.backGrupo1.model.Event event = new com.grupo1.backGrupo1.model.Event();
        event.setId(id);
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setTime(dto.getTime());
        event.setLocation(dto.getLocation());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setMajority18(Boolean.TRUE.equals(dto.getMajority18()));
        return service.saveEvent(event);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}