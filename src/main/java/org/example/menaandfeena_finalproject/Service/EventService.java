package org.example.menaandfeena_finalproject.Service;


import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;


    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }


    public void addEvent(Event event) {
        eventRepository.save(event);
    }


    public void updateEvent(Integer id, Event event) {
        Event oldEvent = eventRepository.findEventById(id);

      if(oldEvent == null){
          throw new ApiException("Event not found");
      }
        oldEvent.setTitle(event.getTitle());
        oldEvent.setDescription(event.getDescription());
        oldEvent.setDate(event.getDate());
        oldEvent.setLocation(event.getLocation());
        oldEvent.setIsPaid(event.getIsPaid());
        oldEvent.setPrice(event.getPrice());
        oldEvent.setMaxParticipants(event.getMaxParticipants());
        oldEvent.setStatus(event.getStatus());

        eventRepository.save(oldEvent);

    }


    public void deleteEvent(Integer id) {
        Event event = eventRepository.findEventById(id);

        if (event == null) {
            throw new ApiException("Event not found");
        }

        eventRepository.delete(event);
    }

// Walaa
    public List<Event> getUpcomingEvents() {
        return eventRepository.findEventsByDateAfter(LocalDateTime.now());
    }




  // Walaa
  public List<Event> getPreviousEvents() {
      return eventRepository.findEventsByDateBefore(LocalDateTime.now());
  }


  // Walaa
  public List<Event> getEventsByDate(LocalDate date) {
      LocalDateTime startOfDay = date.atStartOfDay();
      LocalDateTime endOfDay = date.atTime(23, 59, 59);
      return eventRepository.findEventsByDateBetween(startOfDay, endOfDay);

  }

    // Walaa
    public Event getEventById(Integer id) {
        Event event = eventRepository.findEventById(id);
        if (event == null) {
            throw new ApiException("Event not found");
        }
        return event;
    }




}
