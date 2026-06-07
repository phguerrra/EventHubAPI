package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

    List<Speaker> findByDeletedFalse();

    Optional<Speaker> findByIdAndDeletedFalse(Long id);
}