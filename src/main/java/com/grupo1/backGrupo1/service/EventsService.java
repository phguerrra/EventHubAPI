package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventsService {


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
        if (category != null && !category.isBlank()){
            validateCategory(category);

            return repository.findByCategoryIgnoreCaseAndDeletedFalse(category);
        }
        return repository.findAllByDeletedFalse();
    }

    public List<String> listCategories(){
        return CATEGORIES;
    }

    public List<Event> search(String termo) {

        // Caso o termo venha vazio ou nulo,
        // retorna todos os eventos ativos
        if (termo == null || termo.isBlank()) {
            return repository.findAllByDeletedFalse();
        }

        // Realiza a busca utilizando o termo digitado
        return repository.searchByTermo(termo.trim());
    }

    private void validateCategory(String category){
        boolean isValid = CATEGORIES.stream()
                .anyMatch(c -> c.equalsIgnoreCase(category));

        if(!isValid){
            throw new BusinessRuleException(
                    "Categoria invalida. Use:" + CATEGORIES
            );
        }
    }

    public Event getById(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado com id: " + id));
    }


    public Event saveEvent(Event event) {
        if (event == null) {
            throw new BusinessRuleException("Evento não pode ser nulo");
        }
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new BusinessRuleException("Título do evento é obrigatório");
        }
        if (event.getMaxParticipants() <= 0) {
            throw new BusinessRuleException("O número máximo de participantes deve ser maior que zero");
        }
        if (event.getDate() == null) {
            throw new BusinessRuleException("Data do evento é obrigatória");
        }
        if (event.getParticipants() != null && event.getParticipants().size() > event.getMaxParticipants()) {
            throw new BusinessRuleException("Número de participantes excede o máximo permitido");
        }
        if (event.getCategory() == null || event.getCategory().isBlank()){
            throw new BusinessRuleException(
                    "Categoria do evento é obrigatoria"
            );
        }
        validateCategory(event.getCategory());
        return repository.save(event);
    }

    public void deleteById(Long id) {
        Event event = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado com id: " + id));
        event.setDeleted(true);
        repository.save(event);
    }

    public void cancelRegistration(Long eventId, Long participantId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        event.getParticipants().remove(participant);
        participant.setEvent(null);

        repository.save(event);
    }
}