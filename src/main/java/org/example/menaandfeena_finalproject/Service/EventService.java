package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.FamilyMember;
import org.example.menaandfeena_finalproject.Repository.EventRepository;
import org.example.menaandfeena_finalproject.Repository.FamilyMemberRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.example.menaandfeena_finalproject.DTO.In.EventInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.EventOutDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.example.menaandfeena_finalproject.Model.User;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final OpenAIService openAIService;

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




    // Walaa

    public void createEvent(Integer userId, Event event) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        if (event.getIsPaid().equals(false)) {
            event.setPrice(0.0);
        }

        if (event.getIsPaid().equals(true) && event.getPrice() <= 0) {
            throw new ApiException("Paid event must have price greater than 0");
        }

        event.setUser(user);
        event.setStatus("ACTIVE");

        eventRepository.save(event);
    }


// Walaa

    public String recommendEventForUser(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<FamilyMember> familyMembers = familyMemberRepository.findByUserId(userId);
        System.out.println("FAMILY SIZE = " + familyMembers.size());

        for (FamilyMember member : familyMembers) {
            System.out.println("MEMBER AGE = " + member.getAge());
        }
        List<Event> upcomingEvents = eventRepository.findEventsByDateAfter(LocalDateTime.now());

        if (upcomingEvents.isEmpty()) {
            throw new ApiException("No upcoming events found");
        }

       // return "ready";
        StringBuilder prompt = new StringBuilder();

        prompt.append("Family Members:\n");

        for (FamilyMember member : familyMembers) {
            prompt.append("Name: ")
                    .append(member.getName())
                    .append(" | Age: ")
                    .append(member.getAge())
                    .append(" | Relation: ")
                    .append(member.getRelation())
                    .append("\n");
        }

        prompt.append("\nAvailable Events:\n");

        for (Event event : upcomingEvents) {
            prompt.append("- ")
                    .append(event.getTitle())
                    .append(": ")
                    .append(event.getDescription())
                    .append("\n");
        }
        String aiRecommendation = openAIService.askAI(
                "You are an AI event recommendation system for a smart neighborhood platform. " +
                        "Recommend one suitable event for EACH family member individually based on their age and relation. " +
                        "Use these age groups exactly: children = age 0 to 12, teenagers = age 13 to 18, adults = age 19 and above. " +
                        "Children prefer drawing, games, entertainment, and educational activities. " +
                        "Teenagers prefer sports, technology, competitions, and skill-building activities. " +
                        "Adults prefer community, cultural, educational, and social activities. " +
                        "Choose only from the available events list. " +
                        "Return only in this format:\n" +
                        "Family Member: <name> | Age Group: <children/teenagers/adults> | Recommended Event: <event title> | Reason: <short reason>",
                prompt.toString()
        );

        return aiRecommendation;
    }


















    // Walaa
//    private EventOutDTO convertToOutDTO(Event event) {
//        return new EventOutDTO(
//                event.getId(),
//                event.getTitle(),
//                event.getDescription(),
//                event.getDate(),
//                event.getLocation(),
//                event.getIsPaid(),
//                event.getPrice(),
//                event.getMaxParticipants(),
//                event.getStatus()
//        );
//    }


}
