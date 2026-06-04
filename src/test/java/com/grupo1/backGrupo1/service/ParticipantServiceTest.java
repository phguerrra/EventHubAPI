package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.exception.EntityNotFoundException;
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
}
