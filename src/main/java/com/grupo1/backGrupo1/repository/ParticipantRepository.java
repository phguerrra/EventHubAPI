package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByEventoId(Long eventoId);
    
    Optional<Participant> findByEventoIdAndEmail(Long eventoId, String email);
    
    boolean existsByEventoIdAndEmail(Long eventoId, String email);
}