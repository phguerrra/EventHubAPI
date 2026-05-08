package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.EventDTO;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.service.EventsService;
import com.grupo1.backGrupo1.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventsController {

    private final EventsService service;
    private final FileStorageService fileStorageService;

    public EventsController(EventsService service, FileStorageService fileStorageService) {
        this.service = service;
        this.fileStorageService = fileStorageService;
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
    public Event create(@ModelAttribute @Valid EventDTO dto,
                        @RequestParam("photo") MultipartFile photo,
                        HttpSession session) {
        validarAdmin(session);

        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setTime(dto.getTime());
        event.setLocation(dto.getLocation());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setMajority18(Boolean.TRUE.equals(dto.getMajority18()));
        event.setCategory(dto.getCategory());
        event.setPhotoUrl(fileStorageService.storeEventPhoto(photo));

        return service.saveEvent(event);
    }

    @PutMapping("/{id}")
    public Event update(@PathVariable Long id,
                        @ModelAttribute @Valid EventDTO dto,
                        @RequestParam(value = "photo", required = false) MultipartFile photo,
                        HttpSession session) {
        validarAdmin(session);

        Event event = service.getById(id);
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setTime(dto.getTime());
        event.setLocation(dto.getLocation());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setMajority18(Boolean.TRUE.equals(dto.getMajority18()));
        event.setCategory(dto.getCategory());

        if (photo != null && !photo.isEmpty()) {
            event.setPhotoUrl(fileStorageService.storeEventPhoto(photo));
        }

        return service.saveEvent(event);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpSession session) {
        validarAdmin(session);
        service.deleteById(id);
    }

    private void validarAdmin(HttpSession session) {
        Object userId = session.getAttribute("userId");
        Object userRole = session.getAttribute("userRole");

        if (userId == null) {
            throw new BusinessRuleException("Usuário não está logado");
        }

        if (!"ADMIN".equals(userRole)) {
            throw new BusinessRuleException("Apenas administradores podem realizar esta ação");
        }
    }
}
