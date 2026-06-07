package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.SpeakerDTO;
import com.grupo1.backGrupo1.dto.SpeakerScheduleDTO;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Speaker;
import com.grupo1.backGrupo1.model.SpeakerSchedule;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.SpeakerRepository;
import com.grupo1.backGrupo1.repository.SpeakerScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpeakerService {

    private final SpeakerRepository speakerRepository;
    private final SpeakerScheduleRepository scheduleRepository;
    private final EventsRepository eventsRepository;

    public SpeakerService(SpeakerRepository speakerRepository,
                          SpeakerScheduleRepository scheduleRepository,
                          EventsRepository eventsRepository) {
        this.speakerRepository = speakerRepository;
        this.scheduleRepository = scheduleRepository;
        this.eventsRepository = eventsRepository;
    }

    public List<Speaker> listAll() {
        return speakerRepository.findByDeletedFalse();
    }

    public Speaker getById(Long id) {
        return speakerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Speaker not found with id: " + id));
    }

    @Transactional
    public Speaker create(SpeakerDTO dto) {
        Speaker speaker = new Speaker();
        speaker.setName(dto.getName());
        speaker.setEmail(dto.getEmail());
        speaker.setBio(dto.getBio());
        if (dto.getTopics() != null) speaker.setTopics(dto.getTopics());
        return speakerRepository.save(speaker);
    }

    @Transactional
    public Speaker update(Long id, SpeakerDTO dto) {
        Speaker speaker = getById(id);
        speaker.setName(dto.getName());
        speaker.setEmail(dto.getEmail());
        speaker.setBio(dto.getBio());
        if (dto.getTopics() != null) speaker.setTopics(dto.getTopics());
        return speakerRepository.save(speaker);
    }

    @Transactional
    public void delete(Long id) {
        Speaker speaker = getById(id);
        speaker.setDeleted(true);
        speakerRepository.save(speaker);
    }

    public List<SpeakerSchedule> getSchedule(Long speakerId) {
        getById(speakerId);
        return scheduleRepository.findBySpeakerId(speakerId);
    }

    public List<SpeakerSchedule> getScheduleByEvent(Long eventId) {
        return scheduleRepository.findByEventId(eventId);
    }

    @Transactional
    public SpeakerSchedule addSchedule(Long speakerId, SpeakerScheduleDTO dto) {
        Speaker speaker = getById(speakerId);

        Event event = eventsRepository.findByIdAndDeletedFalse(dto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Event not found with id: " + dto.getEventId()));

        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new BusinessRuleException(
                    "End time must be after start time");
        }

        SpeakerSchedule schedule = new SpeakerSchedule();
        schedule.setSpeaker(speaker);
        schedule.setEvent(event);
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setDescription(dto.getDescription());

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void removeSchedule(Long speakerId, Long scheduleId) {
        getById(speakerId);
        SpeakerSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Schedule item not found with id: " + scheduleId));

        if (!schedule.getSpeaker().getId().equals(speakerId)) {
            throw new BusinessRuleException(
                    "This schedule item does not belong to the given speaker");
        }

        scheduleRepository.deleteById(scheduleId);
    }

    @Transactional
    public Speaker updatePhoto(Long id, String photoUrl) {
        Speaker speaker = getById(id);
        speaker.setPhotoUrl(photoUrl);
        return speakerRepository.save(speaker);
    }
}