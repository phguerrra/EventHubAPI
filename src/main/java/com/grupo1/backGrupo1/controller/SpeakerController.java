package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.SpeakerDTO;
import com.grupo1.backGrupo1.dto.SpeakerScheduleDTO;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.model.Speaker;
import com.grupo1.backGrupo1.model.SpeakerSchedule;
import com.grupo1.backGrupo1.service.FileStorageService;
import com.grupo1.backGrupo1.service.SpeakerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/speakers")
public class SpeakerController {

    private final SpeakerService service;
    private final FileStorageService fileStorageService;

    public SpeakerController(SpeakerService service,
                             FileStorageService fileStorageService) {
        this.service = service;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<List<Speaker>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Speaker> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Speaker> create(
            @RequestBody @Valid SpeakerDTO dto,
            Authentication authentication) {
        validarAdmin(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Speaker> update(
            @PathVariable Long id,
            @RequestBody @Valid SpeakerDTO dto,
            Authentication authentication) {
        validarAdmin(authentication);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        validarAdmin(authentication);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<Speaker> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        validarAdmin(authentication);
        String url = fileStorageService.storeFile(file, "speakers");
        return ResponseEntity.ok(service.updatePhoto(id, url));
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<SpeakerSchedule>> getSchedule(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getSchedule(id));
    }

    @PostMapping("/{id}/schedule")
    public ResponseEntity<SpeakerSchedule> addSchedule(
            @PathVariable Long id,
            @RequestBody @Valid SpeakerScheduleDTO dto,
            Authentication authentication) {
        validarAdmin(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addSchedule(id, dto));
    }

    @DeleteMapping("/{id}/schedule/{scheduleId}")
    public ResponseEntity<Void> removeSchedule(
            @PathVariable Long id,
            @PathVariable Long scheduleId,
            Authentication authentication) {
        validarAdmin(authentication);
        service.removeSchedule(id, scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event/{eventId}/schedule")
    public ResponseEntity<List<SpeakerSchedule>> getScheduleByEvent(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(service.getScheduleByEvent(eventId));
    }

    private void validarAdmin(Authentication authentication) {
        if (authentication == null) {
            throw new BusinessRuleException("User not authenticated");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new BusinessRuleException(
                    "Only administrators can perform this action");
        }
    }
}