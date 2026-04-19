package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findByEventId(Long eventId);

    Optional<Participant> findByEventIdAndEmail(Long eventId, String email);

    boolean existsByEventIdAndEmail(Long eventId, String email);

    long countByEventId(Long eventId);
}