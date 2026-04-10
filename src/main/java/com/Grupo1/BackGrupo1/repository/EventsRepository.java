package com.Grupo1.BackGrupo1.repository;

import com.Grupo1.BackGrupo1.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsRepository extends JpaRepository<Events, Long> {
}