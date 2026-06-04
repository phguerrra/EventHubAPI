package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.EventResponseDTO;
import com.grupo1.backGrupo1.dto.ParticipantResponseDTO;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Participant;
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
    public List<EventResponseDTO> listAll(
            @RequestParam(required = false) String category
    ) {
        return service.listAll(category).stream()
                .map(this::toDTO)
                .toList();
    }

    // =========================================================
    // BUSCA
    // =========================================================

    @GetMapping("/search")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados retornados")
    })
    public List<EventResponseDTO> search(
            @RequestParam(required = false) String q
    ) {
        return service.search(q).stream()
                .map(this::toDTO)
                .toList();
    }

    // =========================================================
    // EVENTO POR ID
    // =========================================================

    @GetMapping("/{id}")
    public EventResponseDTO getById(@PathVariable Long id) {
        return toDTO(service.getById(id));
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Event create(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam String location,
            @RequestParam Integer maxParticipants,
            @RequestParam(defaultValue = "false") Boolean majority18,
            @RequestParam String category,
            @RequestParam(defaultValue = "false") Boolean requiresApproval,
            @RequestParam(required = false) MultipartFile image
    ) throws Exception {

        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setDate(java.time.LocalDate.parse(date));
        event.setTime(java.time.LocalTime.parse(time));
        event.setLocation(location);
        event.setMaxParticipants(maxParticipants);
        event.setMajority18(Boolean.TRUE.equals(majority18));
        event.setCategory(category);
        event.setRequiresApproval(Boolean.TRUE.equals(requiresApproval));

        if (image != null && !image.isEmpty()) {
            event.setImageUrl(saveImage(image));
        }

        return service.saveEvent(event);
    }

    // =========================================================
    // EDITAR EVENTO — atualiza só os campos enviados
    // =========================================================

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Event update(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer maxParticipants,
            @RequestParam(required = false) Boolean majority18,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean requiresApproval,
            @RequestParam(required = false) MultipartFile image
    ) throws Exception {

        // Busca o evento existente — mantém todos os valores atuais
        Event event = service.getById(id);

        // Só atualiza o campo se vier na requisição
        if (title != null && !title.isBlank())       event.setTitle(title);
        if (description != null)                      event.setDescription(description);
        if (date != null)                             event.setDate(java.time.LocalDate.parse(date));
        if (time != null)                             event.setTime(java.time.LocalTime.parse(time));
        if (location != null && !location.isBlank())  event.setLocation(location);
        if (maxParticipants != null)                  event.setMaxParticipants(maxParticipants);
        if (majority18 != null)                       event.setMajority18(majority18);
        if (category != null && !category.isBlank())  event.setCategory(category);
        if (requiresApproval != null)                 event.setRequiresApproval(requiresApproval);

        // Só troca a imagem se vier uma nova
        if (image != null && !image.isEmpty()) {
            event.setImageUrl(saveImage(image));
        }

        return service.saveEvent(event);
    }

    // =========================================================
    // DELETAR EVENTO
    // =========================================================

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    // =========================================================
    // HELPER — salva imagem em disco e retorna a URL
    // =========================================================

    private String saveImage(MultipartFile image) throws Exception {
        String uploadDir = "uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "http://localhost:8080/uploads/" + fileName;
    }

    private EventResponseDTO toDTO(Event event) {
        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getTime(),
                event.getLocation(),
                event.getMaxParticipants(),
                event.isMajority18(),
                event.getCategory(),
                event.isRequiresApproval(),
                event.getImageUrl(),
                event.getParticipants().stream()
                        .filter(participant -> !participant.isDeleted())
                        .map(this::toDTO)
                        .toList()
        );
    }

    private ParticipantResponseDTO toDTO(Participant participant) {
        return new ParticipantResponseDTO(
                participant.getId(),
                participant.getName(),
                participant.getEmail(),
                participant.getPhone(),
                participant.getCpf(),
                participant.getStatus(),
                participant.getDataInscricao(),
                participant.getPresenca()
        );
    }
}
