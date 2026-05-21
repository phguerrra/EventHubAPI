package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByDeletedFalse();

    List<Event> findByCategoryIgnoreCaseAndDeletedFalse(String category);

    java.util.Optional<Event> findByIdAndDeletedFalse(Long id);

    boolean existsByIdAndDeletedFalse(Long id);

    // adicionado

    @Query
            ("""
        SELECT e FROM Event e
        WHERE e.deleted = false
        AND (
            LOWER(e.title)       LIKE LOWER(CONCAT('%', :termo, '%')) OR
            LOWER(e.description) LIKE LOWER(CONCAT('%', :termo, '%')) OR
            LOWER(e.location)    LIKE LOWER(CONCAT('%', :termo, '%')) OR
            LOWER(e.category)    LIKE LOWER(CONCAT('%', :termo, '%')) OR
            CAST(e.date AS string) LIKE CONCAT('%', :termo, '%')
        )
    """)
    List<Event> searchByTermo(@Param("termo") String termo);
}