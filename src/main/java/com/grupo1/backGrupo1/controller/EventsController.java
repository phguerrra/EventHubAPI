package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.service.EventsService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventsController {

    private final EventsService service;

    public EventsController(EventsService service) {
        this.service = service;
    }

    // =========================================================
    // LISTAR EVENTOS
    // =========================================================

    @GetMapping
    public List<Event> listAll(
            @RequestParam(required = false) String category
    ) {

        return service.listAll(category);
    }

    // =========================================================
    // BUSCA
    // =========================================================

    @GetMapping("/search")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultados retornados"
            )
    })
    public List<Event> search(
            @RequestParam(required = false) String q
    ) {

        return service.search(q);
    }

    // =========================================================
    // EVENTO POR ID
    // =========================================================

    @GetMapping("/{id}")
    public Event getById(
            @PathVariable Long id
    ) {

        return service.getById(id);
    }

    // =========================================================
    // CATEGORIAS
    // =========================================================

    @GetMapping("/categories")
    public List<String> listCategories() {

        return service.listCategories();
    }

    // =========================================================
    // CRIAR EVENTO
    // =========================================================

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Event create(

            @RequestParam String title,

            @RequestParam String description,

            @RequestParam String date,

            @RequestParam String time,

            @RequestParam String location,

            @RequestParam Integer maxParticipants,

            @RequestParam Boolean majority18,

            @RequestParam String category,

            @RequestParam(required = false)
            MultipartFile image

    ) throws Exception {

        Event event = new Event();

        event.setTitle(title);

        event.setDescription(description);

        event.setDate(
                java.time.LocalDate.parse(date)
        );

        event.setTime(
                java.time.LocalTime.parse(time)
        );

        event.setLocation(location);

        event.setMaxParticipants(maxParticipants);

        event.setMajority18(
                Boolean.TRUE.equals(majority18)
        );

        event.setCategory(category);

        // =====================================================
        // IMAGEM
        // =====================================================

        if (image != null && !image.isEmpty()) {

            String uploadDir = "uploads/";

            File dir = new File(uploadDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName =
                    UUID.randomUUID()
                            + "_"
                            + image.getOriginalFilename();

            Path filePath =
                    Paths.get(uploadDir, fileName);

            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String imageUrl =
                    "http://localhost:8080/uploads/"
                            + fileName;

            event.setImageUrl(imageUrl);
        }

        return service.saveEvent(event);
    }

    // =========================================================
    // EDITAR EVENTO
    // =========================================================

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Event update(

            @PathVariable Long id,

            @RequestParam String title,

            @RequestParam String description,

            @RequestParam String date,

            @RequestParam String time,

            @RequestParam String location,

            @RequestParam Integer maxParticipants,

            @RequestParam Boolean majority18,

            @RequestParam String category,

            @RequestParam(required = false)
            MultipartFile image

    ) throws Exception {

        Event event =
                service.getById(id);

        event.setTitle(title);

        event.setDescription(description);

        event.setDate(
                java.time.LocalDate.parse(date)
        );

        event.setTime(
                java.time.LocalTime.parse(time)
        );

        event.setLocation(location);

        event.setMaxParticipants(maxParticipants);

        event.setMajority18(
                Boolean.TRUE.equals(majority18)
        );

        event.setCategory(category);

        // =====================================================
        // NOVA IMAGEM
        // =====================================================

        if (image != null && !image.isEmpty()) {

            String uploadDir = "uploads/";

            File dir = new File(uploadDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName =
                    UUID.randomUUID()
                            + "_"
                            + image.getOriginalFilename();

            Path filePath =
                    Paths.get(uploadDir, fileName);

            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String imageUrl =
                    "http://localhost:8080/uploads/"
                            + fileName;

            event.setImageUrl(imageUrl);
        }

        return service.saveEvent(event);
    }

    // =========================================================
    // DELETAR EVENTO
    // =========================================================

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {

        service.deleteById(id);
    }
}