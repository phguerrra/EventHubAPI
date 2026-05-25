package com.grupo1.backGrupo1.service;


import com.grupo1.backGrupo1.model.*;
import com.grupo1.backGrupo1.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventMaterialService {

    private final EventMaterialRepository materialRepo;
    private final ParticipantRepository participantRepo;
    private final EventsRepository eventRepo;

    public EventMaterialService(EventMaterialRepository materialRepo,
                                ParticipantRepository participantRepo,
                                EventsRepository eventRepo) {
        this.materialRepo = materialRepo;
        this.participantRepo = participantRepo;
        this.eventRepo = eventRepo;
    }
    public void verificarAcesso(Long eventId, String email) {
        Participant p = participantRepo
                .findByEmailAndEventId(email, eventId)  // <- ordem: email, eventId
                .orElseThrow(() -> new RuntimeException("Você não está inscrito neste evento"));

        if (p.getStatus() != Participant.Status.APROVADO) {
            throw new RuntimeException("Acesso negado: inscrição não aprovada");
        }
    }

    public List<EventMaterial> listMaterials(Long eventId, String email) {
        verificarAcesso(eventId, email);
        return materialRepo.findByEventId(eventId);
    }

    public EventMaterial uploadMaterial(Long eventId, String title,
                                        String description, MultipartFile file) throws Exception {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        String uploadDir = "uploads/materials/";
        new File(uploadDir).mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        EventMaterial material = new EventMaterial();
        material.setTitle(title);
        material.setDescription(description);
        material.setFileUrl("http://localhost:8080/uploads/materials/" + fileName);
        material.setFileType(file.getContentType());
        material.setUploadedAt(LocalDateTime.now());
        material.setEvent(event);

        return materialRepo.save(material);
    }

    public void deleteMaterial(Long materialId) {
        materialRepo.deleteById(materialId);
    }
}
