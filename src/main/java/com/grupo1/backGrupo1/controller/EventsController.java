package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.EventDTO;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.service.EventsService;
import com.grupo1.backGrupo1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventsController {

    private final EventsService service;
    private final UserService userService;

    public EventsController(EventsService service, UserService userService) {
        this.service = service;
        this.userService = userService;
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
    @com.grupo1.backGrupo1.security.AdminOnly
    public Event create(@RequestParam Long userId, @RequestBody @Valid EventDTO dto) {
        Event event = new Event();
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
    @com.grupo1.backGrupo1.security.AdminOnly
    public Event update(@PathVariable Long id, @RequestParam Long userId, @RequestBody @Valid EventDTO dto) {
        Event event = new Event();
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
    @com.grupo1.backGrupo1.security.AdminOnly
    public void delete(@PathVariable Long id, @RequestParam Long userId) {
        service.deleteById(id);
    }

    @DeleteMapping("/{eventId}/participants/{participantId}/cancel")
    public void cancelRegistration(@PathVariable Long eventId,
                                   @PathVariable Long participantId) {
        service.cancelRegistration(eventId, participantId);
    }
}