package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class EventsService {

    private static final Logger logger = LoggerFactory.getLogger(EventsService.class);

    private static final List<String> CATEGORIES = List.of(
            "Tecnologia",
            "Educação",
            "Música",
            "Esporte",
            "Entretenimento"
    );

    private final EventsRepository repository;
    private final ParticipantRepository participantRepository;

    public EventsService(EventsRepository repository, ParticipantRepository participantRepository) {
        this.repository = repository;
        this.participantRepository = participantRepository;
    }

    public List<Event> listAll(String category) {

        logger.info("Listando eventos. Filtro de categoria: {}", category);

        if (category != null && !category.isBlank()) {
            validateCategory(category);
            return repository.findByCategoryIgnoreCaseAndDeletedFalse(category);
        }

        return repository.findAllByDeletedFalse();
    }

    public List<String> listCategories() {
        logger.info("Listando categorias disponíveis");
        return CATEGORIES;
    }

    private void validateCategory(String category) {

        boolean isValid = CATEGORIES.stream()
                .anyMatch(c -> c.equalsIgnoreCase(category));

        if (!isValid) {
            logger.warn("Categoria inválida recebida: {}", category);
            throw new BusinessRuleException(
                    "Categoria invalida. Use:" + CATEGORIES
            );
        }
    }

    public Event getById(Long id) {

        logger.info("Buscando evento por ID: {}", id);

        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.warn("Evento não encontrado com id: {}", id);
                    return new EntityNotFoundException("Evento não encontrado com id: " + id);
                });
    }

    public Event saveEvent(Event event) {

        try {
            logger.info("Tentando salvar evento: {}", event != null ? event.getTitle() : "null");

            if (event == null) {
                logger.warn("Tentativa de salvar evento nulo");
                throw new BusinessRuleException("Evento não pode ser nulo");
            }

            if (event.getTitle() == null || event.getTitle().isBlank()) {
                logger.warn("Evento sem título");
                throw new BusinessRuleException("Título do evento é obrigatório");
            }

            if (event.getMaxParticipants() <= 0) {
                logger.warn("MaxParticipants inválido: {}", event.getMaxParticipants());
                throw new BusinessRuleException("O número máximo de participantes deve ser maior que zero");
            }

            if (event.getDate() == null) {
                logger.warn("Evento sem data");
                throw new BusinessRuleException("Data do evento é obrigatória");
            }

            if (event.getParticipants() != null && event.getParticipants().size() > event.getMaxParticipants()) {
                logger.warn("Evento excedeu limite de participantes");
                throw new BusinessRuleException("Número de participantes excede o máximo permitido");
            }

            if (event.getCategory() == null || event.getCategory().isBlank()) {
                logger.warn("Evento sem categoria");
                throw new BusinessRuleException("Categoria do evento é obrigatoria");
            }

            validateCategory(event.getCategory());

            Event saved = repository.save(event);

            logger.info("Evento salvo com sucesso: {}", saved.getTitle());

            return saved;

        } catch (Exception e) {
            logger.error("Erro ao salvar evento: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void deleteById(Long id) {

        try {
            logger.info("Deletando evento (soft delete) ID: {}", id);

            Event event = repository.findByIdAndDeletedFalse(id)
                    .orElseThrow(() -> {
                        logger.warn("Evento não encontrado para delete: {}", id);
                        return new EntityNotFoundException("Evento não encontrado com id: " + id);
                    });

            event.setDeleted(true);
            repository.save(event);

            logger.info("Evento deletado (soft delete) com sucesso: {}", id);

        } catch (Exception e) {
            logger.error("Erro ao deletar evento ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void cancelRegistration(Long eventId, Long participantId) {

        try {
            logger.info("Cancelando inscrição - EventId: {}, ParticipantId: {}", eventId, participantId);

            Event event = repository.findById(eventId)
                    .orElseThrow(() -> {
                        logger.warn("Evento não encontrado: {}", eventId);
                        return new RuntimeException("Event not found");
                    });

            Participant participant = participantRepository.findById(participantId)
                    .orElseThrow(() -> {
                        logger.warn("Participante não encontrado: {}", participantId);
                        return new RuntimeException("Participant not found");
                    });

            event.getParticipants().remove(participant);
            participant.setEvent(null);

            repository.save(event);

            logger.info("Inscrição cancelada com sucesso");

        } catch (Exception e) {
            logger.error("Erro ao cancelar inscrição: {}", e.getMessage(), e);
            throw e;
        }
    }
}