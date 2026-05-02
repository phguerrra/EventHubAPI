package com.grupo1.backGrupo1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.grupo1.backGrupo1.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketId(String ticketId);
}
