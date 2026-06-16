package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.exception.EmailSendException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.ParticipantRepository;
import com.grupo1.backGrupo1.repository.UserRepository;
import com.grupo1.backGrupo1.service.email.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventsRepository eventsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ParticipantService service;

    @Test
    void removeParticipantFromEvent_marksParticipantAsDeleted() {
        Participant participant = new Participant();
        participant.setId(10L);
        participant.setDeleted(false);

        when(participantRepository.findByEventIdAndIdAndDeletedFalse(1L, 10L))
                .thenReturn(Optional.of(participant));

        service.removeParticipantFromEvent(1L, 10L);

        assertTrue(participant.isDeleted());
        verify(participantRepository).findByEventIdAndIdAndDeletedFalse(1L, 10L);
        verify(participantRepository).save(participant);
    }

    @Test
    void removeParticipantFromEvent_throwsWhenParticipantDoesNotBelongToEvent() {
        when(participantRepository.findByEventIdAndIdAndDeletedFalse(1L, 10L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> service.removeParticipantFromEvent(1L, 10L)
        );

        verify(participantRepository).findByEventIdAndIdAndDeletedFalse(1L, 10L);
        verifyNoMoreInteractions(participantRepository);
    }

    @Test
    void aprovarInscricao_doesNotThrowWhenConfirmationEmailFails() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Workshop");
        event.setMaxParticipants(10);

        Participant participant = new Participant();
        participant.setId(10L);
        participant.setName("Pedro");
        participant.setEmail("pedro@example.com");
        participant.setStatus(Participant.Status.PENDENTE);
        participant.setEvent(event);

        when(participantRepository.findById(10L)).thenReturn(Optional.of(participant));
        when(participantRepository.countByEventIdAndDeletedFalseAndStatus(1L, Participant.Status.APROVADO))
                .thenReturn(0L);
        when(participantRepository.save(participant)).thenReturn(participant);
        doThrow(new EmailSendException("Resend API key is not configured"))
                .when(emailService)
                .sendConfirmationEmail(eq("pedro@example.com"), eq("Inscrição aprovada — Workshop"), contains("foi aprovada"));

        // não deve lançar exceção — falha de email é silenciosa
        assertDoesNotThrow(() -> service.aprovarInscricao(1L, 10L));
    }

    @Test
    void aprovarInscricao_throwsWhenConfirmationEmailFails() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Workshop");
        event.setMaxParticipants(10);

        Participant participant = new Participant();
        participant.setId(10L);
        participant.setName("Pedro");
        participant.setEmail("pedro@example.com");
        participant.setStatus(Participant.Status.PENDENTE);
        participant.setEvent(event);

        when(participantRepository.findById(10L)).thenReturn(Optional.of(participant));
        when(participantRepository.countByEventIdAndDeletedFalseAndStatus(1L, Participant.Status.APROVADO))
                .thenReturn(0L);
        when(participantRepository.save(participant)).thenReturn(participant);
        doThrow(new EmailSendException("Resend API key is not configured"))
                .when(emailService)
                .sendConfirmationEmail(eq("pedro@example.com"), eq("Inscrição aprovada — Workshop"), contains("foi aprovada"));

        assertThrows(EmailSendException.class, () -> service.aprovarInscricao(1L, 10L));
    }
}
