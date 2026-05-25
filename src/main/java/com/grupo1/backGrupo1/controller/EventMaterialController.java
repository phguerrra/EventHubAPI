package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.model.EventMaterial;
import com.grupo1.backGrupo1.service.EventMaterialService;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/materials")
public class EventMaterialController {

    private final EventMaterialService service;

    public EventMaterialController(EventMaterialService service) {
        this.service = service;
    }

    // Participante APROVADO lista materiais
    @GetMapping
    public ResponseEntity<List<EventMaterial>> list(
            @PathVariable Long eventId,
            Authentication auth) {

        return ResponseEntity.ok(
                service.listMaterials(eventId, auth.getName())
        );
    }

    // Apenas ADMIN faz upload — proteja na SecurityConfig
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventMaterial> upload(
            @PathVariable Long eventId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam MultipartFile file) throws Exception {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.uploadMaterial(eventId, title, description, file));
    }

    // Apenas ADMIN deleta
    @DeleteMapping("/{materialId}")
    public ResponseEntity<?> delete(@PathVariable Long materialId) {
        service.deleteMaterial(materialId);
        return ResponseEntity.noContent().build();
    }
}