package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.*;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.ParticipantRepository;
import com.grupo1.backGrupo1.repository.UserRepository;
import com.grupo1.backGrupo1.service.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(ParticipantService.class);

    public ParticipantService(ParticipantRepository participantRepository,
                              EventsRepository eventsRepository,
                              UserRepository userRepository,
                              EmailService emailService) {
        this.participantRepository = participantRepository;
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // SOLICITAÇÃO DE INSCRIÇÃO (fica PENDENTE)
    @Transactional
    public Participant registerForEvent(Long eventId, Participant participant, Long userId) {

        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado com id: " + eventId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));

        // Checa lotação apenas entre APROVADOS
        long totalAprovados = participantRepository.countByEventIdAndDeletedFalseAndStatus(eventId, Participant.Status.APROVADO);
        if (totalAprovados >= event.getMaxParticipants()) {
            throw new BusinessRuleException("Evento lotado");
        }

        // Não permite segunda solicitação
        if (participantRepository.existsByEventIdAndEmailAndDeletedFalse(eventId, user.getEmail())) {
            throw new BusinessRuleException("Você já possui uma inscrição (pendente ou aprovada) neste evento");
        }

        if (event.isMajority18()) {
            int idade = Period.between(user.getDataNascimento(), LocalDate.now()).getYears();
            if (idade < 18) {
                throw new BusinessRuleException("Este evento é restrito a maiores de 18 anos. O usuário tem " + idade + " anos");
            }
        }

        // Preenche com dados reais do usuário
        participant.setName(user.getName());
        participant.setEmail(user.getEmail());
        participant.setCpf(user.getCpf() != null ? user.getCpf() : "");
        participant.setPhone(user.getPhone() != null ? user.getPhone() : "");
        participant.setEvent(event);
        participant.setStatus(Participant.Status.PENDENTE);
        participant.setPresenca(Participant.Presenca.PENDENTE);
        // dataInscricao fica null até o admin aprovar

        return participantRepository.save(participant);
    }

    // APROVAR INSCRIÇÃO (admin)
    @Transactional
    public Participant aprovarInscricao(Long eventId, Long participantId) {

        Participant participant = buscarParticipanteAtivo(eventId, participantId);

        if (participant.getStatus() == Participant.Status.APROVADO) {
            throw new BusinessRuleException("Inscrição já está aprovada");
        }
        if (participant.getStatus() == Participant.Status.REJEITADO) {
            throw new BusinessRuleException("Não é possível aprovar uma inscrição rejeitada");
        }

        // Revalida lotação no momento da aprovação
        long totalAprovados = participantRepository.countByEventIdAndDeletedFalseAndStatus(eventId, Participant.Status.APROVADO);
        if (totalAprovados >= participant.getEvent().getMaxParticipants()) {
            throw new BusinessRuleException("Evento lotado — não é possível aprovar mais inscrições");
        }

        participant.setStatus(Participant.Status.APROVADO);
        participant.setDataInscricao(LocalDateTime.now());

        Participant saved = participantRepository.save(participant);

        // E-mail de confirmação após commit
        String subject = "Inscrição aprovada — " + participant.getEvent().getTitle();
        String html = "<h1>Inscrição aprovada!</h1>"
                + "<p>Olá, " + saved.getName() + "!</p>"
                + "<p>Sua inscrição no evento <strong>" + saved.getEvent().getTitle() + "</strong> foi aprovada.</p>"
                + "<p>Data: " + saved.getDataInscricao() + "</p>";

        enviarEmailAposCommit(saved.getEmail(), subject, html);

        return saved;
    }

    // REJEITAR INSCRIÇÃO (admin)
    @Transactional
    public Participant rejeitarInscricao(Long eventId, Long participantId) {

        Participant participant = buscarParticipanteAtivo(eventId, participantId);

        if (participant.getStatus() == Participant.Status.REJEITADO) {
            throw new BusinessRuleException("Inscrição já está rejeitada");
        }

        participant.setStatus(Participant.Status.REJEITADO);

        Participant saved = participantRepository.save(participant);

        String subject = "Inscrição não aprovada — " + participant.getEvent().getTitle();
        String html = "<h1>Inscrição não aprovada</h1>"
                + "<p>Olá, " + saved.getName() + "!</p>"
                + "<p>Infelizmente sua inscrição no evento <strong>" + saved.getEvent().getTitle() + "</strong> não foi aprovada.</p>";

        enviarEmailAposCommit(saved.getEmail(), subject, html);

        return saved;
    }

    // MARCAR PRESENÇA (admin)
    @Transactional
    public Participant marcarPresenca(Long eventId, Long participantId, Participant.Presenca presenca) {

        Participant participant = buscarParticipanteAtivo(eventId, participantId);

        if (participant.getStatus() != Participant.Status.APROVADO) {
            throw new BusinessRuleException("Só é possível marcar presença de participantes aprovados");
        }

        participant.setPresenca(presenca);
        return participantRepository.save(participant);
    }

    // LISTAGEM COM FILTRO E ORDENAÇÃO
    public List<Participant> listParticipantsForEvent(Long eventId, Participant.Status status, String orderBy) {

        Sort sort = resolverOrdenacao(orderBy);

        if (status != null) {
            return participantRepository.findByEventIdAndDeletedFalseAndStatus(eventId, status, sort);
        }

        return participantRepository.findByEventIdAndDeletedFalse(eventId, sort);
    }

    // REMOVER PARTICIPANTE
    public void removeParticipantById(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participante não encontrado com id: " + participantId));

        if (participant.isDeleted()) {
            throw new EntityNotFoundException("Participante não encontrado com id: " + participantId);
        }

        participant.setDeleted(true);
        participantRepository.save(participant);
    }

    // HELPERS
    public boolean isEmailRegistered(Long eventId, String email) {
        return participantRepository.existsByEventIdAndEmailAndDeletedFalse(eventId, email);
    }

    public List<Participant> listInscricoesByEmail(String email) {
        return participantRepository.findByEmailAndDeletedFalse(email);
    }

    public Participant findParticipantByEventAndEmail(Long eventId, String email) {
        return participantRepository.findByEventIdAndEmailAndDeletedFalse(eventId, email)
                .orElseThrow(() -> new EntityNotFoundException("Participante não encontrado para este evento"));
    }

    private Participant buscarParticipanteAtivo(Long eventId, Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participante não encontrado com id: " + participantId));

        if (participant.isDeleted()) {
            throw new EntityNotFoundException("Participante não encontrado com id: " + participantId);
        }

        if (!participant.getEvent().getId().equals(eventId)) {
            throw new BusinessRuleException("Participante não pertence a este evento");
        }

        return participant;
    }

    private Sort resolverOrdenacao(String orderBy) {
        if (orderBy == null) return Sort.unsorted();
        return switch (orderBy.toLowerCase()) {
            case "nome"         -> Sort.by(Sort.Direction.ASC, "name");
            case "datainscricao"-> Sort.by(Sort.Direction.DESC, "dataInscricao");
            case "presenca"     -> Sort.by(Sort.Direction.ASC, "presenca");
            default             -> Sort.unsorted();
        };
    }

    private void enviarEmailAposCommit(String email, String subject, String html) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        emailService.sendConfirmationEmail(email, subject, html);
                    } catch (Exception e) {
                        logger.error("Erro ao enviar email para {}", email, e);
                    }
                }
            });
        } else {
            try {
                emailService.sendConfirmationEmail(email, subject, html);
            } catch (Exception e) {
                logger.error("Erro ao enviar email para {}", email, e);
            }
        }
    }
}