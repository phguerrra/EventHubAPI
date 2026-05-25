package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.EventMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventMaterialRepository extends JpaRepository<EventMaterial, Long> {
    List<EventMaterial> findByEventId(Long eventId);
}