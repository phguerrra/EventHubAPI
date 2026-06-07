package com.grupo1.backGrupo1.repository;

import com.grupo1.backGrupo1.model.SpeakerSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpeakerScheduleRepository extends JpaRepository<SpeakerSchedule, Long> {

    List<SpeakerSchedule> findBySpeakerId(Long speakerId);

    List<SpeakerSchedule> findByEventId(Long eventId);
}