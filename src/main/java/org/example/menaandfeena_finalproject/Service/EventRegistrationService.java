package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.Out.EventRegistrationOutDTO;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.EventRegistration;
import org.example.menaandfeena_finalproject.Model.FamilyMember;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.EventRegistrationRepository;
import org.example.menaandfeena_finalproject.Repository.EventRepository;
import org.example.menaandfeena_finalproject.Repository.FamilyMemberRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {
    private final EventRegistrationRepository eventRegistrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final FamilyMemberRepository familyMemberRepository;


    public List<EventRegistrationOutDTO> getAllEventRegistrations() {
        return eventRegistrationRepository.findAll()
                .stream()
                .map(this::convertToOutDTO)
                .toList();
    }

    public void addEventRegistration(EventRegistration eventRegistration) {
        eventRegistrationRepository.save(eventRegistration);
    }

    public void updateEventRegistration(Integer id, EventRegistration eventRegistration) {

        EventRegistration oldEventRegistration = eventRegistrationRepository.findEventRegistrationById(id);

        if (oldEventRegistration == null) {
            throw new ApiException("Event registration not found");
        }

        oldEventRegistration.setStatus(eventRegistration.getStatus());
        oldEventRegistration.setRegisteredAt(eventRegistration.getRegisteredAt());
      //  oldEventRegistration.setUser(eventRegistration.getUser());
        oldEventRegistration.setEvent(eventRegistration.getEvent());

        eventRegistrationRepository.save(oldEventRegistration);
    }

    public void deleteEventRegistration(Integer id) {

        EventRegistration eventRegistration = eventRegistrationRepository.findEventRegistrationById(id);

        if (eventRegistration == null) {
            throw new ApiException("Event registration not found");
        }

        eventRegistrationRepository.delete(eventRegistration);
    }

   // Walaa
   public void registerToEvent(Integer userId, Integer eventId) {
       User user = userRepository.findUserById(userId);
       if (user == null) {
           throw new ApiException("User not found");
       }

       Event event = eventRepository.findEventById(eventId);

       if (event == null) {
           throw new ApiException("Event not found");
       }

       EventRegistration registration = new EventRegistration();

       registration.setUser(user);
       registration.setEvent(event);

       if (event.getIsPaid()) {
           registration.setStatus("PENDING");
       } else {
           registration.setStatus("CONFIRMED");
       }

       registration.setRegisteredAt(LocalDate.now());

       eventRegistrationRepository.save(registration);
   }


   // Walaa

    public void registerFamilyMember(Integer familyMemberId, Integer eventId) {
        FamilyMember familyMember = familyMemberRepository.findFamilyMemberById(familyMemberId);

        if (familyMember == null) {
            throw new ApiException("Family member not found");
        }

        Event event = eventRepository.findEventById(eventId);

        if (event == null) {
            throw new ApiException("Event not found");
        }
        EventRegistration registration = new EventRegistration();
        registration.setUser(familyMember.getUser());
        registration.setEvent(event);

        if (event.getIsPaid()) {
            registration.setStatus("PENDING");
        } else {
            registration.setStatus("CONFIRMED");
        }
        registration.setRegisteredAt(LocalDate.now());
        eventRegistrationRepository.save(registration);

    }


// Walaa
private EventRegistrationOutDTO convertToOutDTO(EventRegistration registration) {
    return new EventRegistrationOutDTO(
            registration.getId(),
            registration.getStatus(),
            registration.getRegisteredAt(),
            registration.getUser().getId(),
            registration.getUser().getFullName(),
            registration.getEvent().getId(),
            registration.getEvent().getTitle(),
            registration.getEvent().getIsPaid(),
            registration.getEvent().getPrice()
    );
}





}
