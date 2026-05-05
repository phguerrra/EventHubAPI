package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.exception.BusinessRuleException;
import org.springframework.transaction.annotation.Transactional;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.ParticipantRepository;
import com.grupo1.backGrupo1.repository.UserRepository;
import com.grupo1.backGrupo1.util.CpfValidator;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;

    public ParticipantService(ParticipantRepository participantRepository, EventsRepository eventsRepository, UserRepository userRepository) {
        this.participantRepository = participantRepository;
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Participant registerForEvent(Long eventId, Participant participant, Long userId) {

        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado com id: " + eventId));

        // Busca o usuário pelo userId para pegar os dados reais
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));

        long totalInscritos = participantRepository.countByEventId(eventId);
        if (totalInscritos >= event.getMaxParticipants()) {
            throw new BusinessRuleException("Evento lotado");
        }

        if (participantRepository.existsByEventIdAndEmail(eventId, user.getEmail())) {
            throw new BusinessRuleException("Email já inscrito neste evento");
        }

        if (event.isMajority18()) {
            int idade = Period.between(user.getDataNascimento(), LocalDate.now()).getYears();
            if (idade < 18) {
                throw new BusinessRuleException("Este evento é restrito a maiores de 18 anos. O usuário tem " + idade + " anos");
            }
        }

        // Sobrescreve com dados reais do banco — ignora o que o front enviou
        participant.setName(user.getName());
        participant.setEmail(user.getEmail());
        participant.setCpf(user.getCpf() != null ? user.getCpf() : "");
        participant.setPhone(user.getPhone() != null ? user.getPhone() : "");
        participant.setEvent(event);

        return participantRepository.save(participant);
    }

    public List<Participant> listParticipantsForEvent(Long eventId) {
        return participantRepository.findByEventId(eventId);
    }

    public void removeParticipantById(Long participantId) {
        if (!participantRepository.existsById(participantId)) {
            throw new EntityNotFoundException("Participante não encontrado com id: " + participantId);
        }
        participantRepository.deleteById(participantId);
    }

    public boolean isEmailRegistered(Long eventId, String email) {
        return participantRepository.existsByEventIdAndEmail(eventId, email);
    }

    public Participant findParticipantByEventAndEmail(Long eventId, String email) {
        return participantRepository.findByEventIdAndEmail(eventId, email)
                .orElseThrow(() -> new EntityNotFoundException("Participante não encontrado para este evento"));
    }
}