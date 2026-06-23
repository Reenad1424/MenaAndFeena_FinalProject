package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.EventScheduleInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.EventScheduleOutDTO;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.EventSchedule;
import org.example.menaandfeena_finalproject.Repository.EventRepository;
import org.example.menaandfeena_finalproject.Repository.EventScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventScheduleService {

    private static final DateTimeFormatter SCHEDULE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final EventScheduleRepository eventScheduleRepository;
    private final EventRepository eventRepository;

    public EventScheduleOutDTO addScheduleItem(Integer eventId, EventScheduleInDTO dto) {
        Event event = getEventOrThrow(eventId);
        LocalTime parsedTime = validateAndParseTime(dto, event);

        EventSchedule schedule = new EventSchedule();
        schedule.setEvent(event);
        schedule.setTime(parsedTime);
        schedule.setTitle(dto.getTitle().trim());
        schedule.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : nextSortOrder(eventId));

        return mapToOutDTO(eventScheduleRepository.save(schedule));
    }

    public EventScheduleOutDTO updateScheduleItem(Integer scheduleId, EventScheduleInDTO dto) {
        EventSchedule schedule = getScheduleOrThrow(scheduleId);
        LocalTime parsedTime = validateAndParseTime(dto, schedule.getEvent());

        schedule.setTime(parsedTime);
        schedule.setTitle(dto.getTitle().trim());
        schedule.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : schedule.getSortOrder());

        return mapToOutDTO(eventScheduleRepository.save(schedule));
    }

    public void deleteScheduleItem(Integer scheduleId) {
        EventSchedule schedule = getScheduleOrThrow(scheduleId);
        eventScheduleRepository.delete(schedule);
    }

    public List<EventScheduleOutDTO> getEventSchedule(Integer eventId) {
        getEventOrThrow(eventId);

        List<EventScheduleOutDTO> out = new ArrayList<>();
        for (EventSchedule schedule : eventScheduleRepository.findByEvent_IdOrderBySortOrderAscTimeAsc(eventId)) {
            out.add(mapToOutDTO(schedule));
        }
        return out;
    }

    private Event getEventOrThrow(Integer eventId) {
        Event event = eventRepository.findEventById(eventId);
        if (event == null) {
            throw new ApiException("Event not found");
        }
        return event;
    }

    private EventSchedule getScheduleOrThrow(Integer scheduleId) {
        EventSchedule schedule = eventScheduleRepository.findEventScheduleById(scheduleId);
        if (schedule == null) {
            throw new ApiException("Event schedule item not found");
        }
        return schedule;
    }

    private LocalTime validateAndParseTime(EventScheduleInDTO dto, Event event) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new ApiException("Schedule title cannot be blank");
        }

        LocalTime parsedTime;
        try {
            parsedTime = LocalTime.parse(dto.getTime(), SCHEDULE_TIME_FORMAT);
        } catch (Exception e) {
            throw new ApiException("Schedule time must be valid HH:mm: " + dto.getTime());
        }

        LocalTime startTime = event == null || event.getDate() == null ? null : event.getDate().toLocalTime();
        LocalTime endTime = event == null || event.getEndTime() == null ? null : event.getEndTime().toLocalTime();
        if (startTime != null && endTime != null
                && (parsedTime.isBefore(startTime) || parsedTime.isAfter(endTime))) {
            throw new ApiException("Schedule time " + dto.getTime() + " is outside the event time range");
        }

        return parsedTime;
    }

    private Integer nextSortOrder(Integer eventId) {
        List<EventSchedule> existing = eventScheduleRepository.findByEvent_IdOrderBySortOrderAscTimeAsc(eventId);
        if (existing.isEmpty()) {
            return 0;
        }
        return existing.get(existing.size() - 1).getSortOrder() + 1;
    }

    private EventScheduleOutDTO mapToOutDTO(EventSchedule schedule) {
        return new EventScheduleOutDTO(
                schedule.getId(),
                schedule.getTime().format(SCHEDULE_TIME_FORMAT),
                schedule.getTitle(),
                schedule.getSortOrder()
        );
    }
}
