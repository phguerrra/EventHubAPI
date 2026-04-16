package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsRepository extends JpaRepository<Event, Long> {
}