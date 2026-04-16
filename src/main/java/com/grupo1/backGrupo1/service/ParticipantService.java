package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.ParticipantRepository;
import com.grupo1.backGrupo1.repository.UserRepository;
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

    public Participant inscreverNoEvento(Long eventoId, Participant participant, Long userId) {

        // Verifica se o evento existe
        Event evento = eventsRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventoId));

        if (participantRepository.existsByEventoIdAndEmail(eventoId, participant.getEmail())) {
            throw new RuntimeException("Este email já está inscrito no evento: " + evento.getTitle());
        }

        if (evento.getParticipants().size() >= evento.getMaxParticipants()) {
            throw new RuntimeException("O evento atingiu o número máximo de participantes");
        }

        // Se o evento é +18, verifica a maioridade do usuário
        if (evento.isMaioridade18()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            int idade = Period.between(user.getDataNascimento(), LocalDate.now()).getYears();
            if (idade < 18) {
                throw new RuntimeException("Este evento é restrito a maiores de 18 anos. Você tem " + idade + " anos");
            }
        }

        participant.setEvento(evento);

        return participantRepository.save(participant);
    }

    public List<Participant> listarParticipantesDoEvento(Long eventoId) {
        return participantRepository.findByEventoId(eventoId);
    }

    public void removerParticipante(Long participanteId) {
        if (!participantRepository.existsById(participanteId)) {
            throw new RuntimeException("Participante não encontrado com ID: " + participanteId);
        }
        participantRepository.deleteById(participanteId);
    }

    public boolean emailJaInscrito(Long eventoId, String email) {
        return participantRepository.existsByEventoIdAndEmail(eventoId, email);
    }

    public Participant buscarParticipante(Long eventoId, String email) {
        return participantRepository.findByEventoIdAndEmail(eventoId, email)
                .orElseThrow(() -> new RuntimeException("Participante não encontrado para este evento"));
    }
}
