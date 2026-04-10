package com.Grupo1.BackGrupo1.repository;

import com.Grupo1.BackGrupo1.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByEventoId(Long eventoId);
}