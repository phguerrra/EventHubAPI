package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findByEventIdAndDeletedFalse(Long eventId);

    Optional<Participant> findByEventIdAndEmailAndDeletedFalse(Long eventId, String email);

    boolean existsByEventIdAndEmailAndDeletedFalse(Long eventId, String email);

    long countByEventIdAndDeletedFalse(Long eventId);

    boolean existsByIdAndDeletedFalse(Long id);
}