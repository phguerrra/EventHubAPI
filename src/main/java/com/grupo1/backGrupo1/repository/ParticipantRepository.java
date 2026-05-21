package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.Participant;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findByEventIdAndDeletedFalse(Long eventId);

    // Listagem com ordenação
    List<Participant> findByEventIdAndDeletedFalse(Long eventId, Sort sort);

    // Listagem filtrada por status
    List<Participant> findByEventIdAndDeletedFalseAndStatus(Long eventId, Participant.Status status);

    // Listagem filtrada por status + ordenação
    List<Participant> findByEventIdAndDeletedFalseAndStatus(Long eventId, Participant.Status status, Sort sort);

    // Contagem de inscritos ativos (APROVADO) para checar lotação
    long countByEventIdAndDeletedFalseAndStatus(Long eventId, Participant.Status status);

    // Mantidos para compatibilidade com código existente
    long countByEventIdAndDeletedFalse(Long eventId);

    boolean existsByEventIdAndEmailAndDeletedFalse(Long eventId, String email);

    Optional<Participant> findByEventIdAndEmailAndDeletedFalse(Long eventId, String email);

    // Eventos inscritos(pendentes, aprovados ou rejeitados) por e-mail logado
    List<Participant> findByEmailAndDeletedFalse(String email);

    @Query("""
        SELECT p FROM Participant p
        WHERE p.deleted = false
        AND p.event.id = :eventId
        AND (
            LOWER(p.name)  LIKE LOWER(CONCAT('%', :termo, '%')) OR
            LOWER(p.email) LIKE LOWER(CONCAT('%', :termo, '%')) OR
            LOWER(p.cpf)   LIKE LOWER(CONCAT('%', :termo, '%'))
        )
    """)
    List<Participant> searchByTermo(@Param("eventId") Long eventId, @Param("termo") String termo);
}