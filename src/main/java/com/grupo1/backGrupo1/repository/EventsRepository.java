package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByDeletedFalse();

    List<Event> findByCategoryIgnoreCaseAndDeletedFalse(String category);

    java.util.Optional<Event> findByIdAndDeletedFalse(Long id);

    boolean existsByIdAndDeletedFalse(Long id);
}