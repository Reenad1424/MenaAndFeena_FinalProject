package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.EventRegistrationInDTO;
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

    public void addEventRegistration(EventRegistrationInDTO dto) {
        User user = userRepository.findUserById(dto.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }

        Event event = eventRepository.findEventById(dto.getEventId());
        if (event == null) {
            throw new ApiException("Event not found");
        }

        EventRegistration eventRegistration = new EventRegistration();
        if (Boolean.TRUE.equals(event.getIsPaid())) {
            eventRegistration.setStatus("PENDING");
        } else {
            eventRegistration.setStatus("CONFIRMED");
        }
        eventRegistration.setRegisteredAt(LocalDate.now());
        eventRegistration.setUser(user);
        eventRegistration.setEvent(event);

        if (dto.getFamilyMemberId() != null) {
            FamilyMember familyMember = familyMemberRepository.findFamilyMemberById(dto.getFamilyMemberId());
            if (familyMember == null) {
                throw new ApiException("Family member not found");
            }
            if (familyMember.getUser() == null || !familyMember.getUser().getId().equals(dto.getUserId())) {
                throw new ApiException("Family member does not belong to this user");
            }
            eventRegistration.setFamilyMember(familyMember);
        }

        eventRegistrationRepository.save(eventRegistration);
    }

    public void updateEventRegistration(Integer id, EventRegistrationInDTO dto) {

        EventRegistration oldEventRegistration = eventRegistrationRepository.findEventRegistrationById(id);

        if (oldEventRegistration == null) {
            throw new ApiException("Event registration not found");
        }

        User user = userRepository.findUserById(dto.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }

        Event event = eventRepository.findEventById(dto.getEventId());
        if (event == null) {
            throw new ApiException("Event not found");
        }

        oldEventRegistration.setUser(user);
        oldEventRegistration.setEvent(event);

        if (dto.getFamilyMemberId() == null) {
            oldEventRegistration.setFamilyMember(null);
        } else {
            FamilyMember familyMember = familyMemberRepository.findFamilyMemberById(dto.getFamilyMemberId());
            if (familyMember == null) {
                throw new ApiException("Family member not found");
            }
            if (familyMember.getUser() == null || !familyMember.getUser().getId().equals(dto.getUserId())) {
                throw new ApiException("Family member does not belong to this user");
            }
            oldEventRegistration.setFamilyMember(familyMember);
        }

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
       if (user.getNeighborhood() == null) {
           throw new ApiException("User neighborhood is required");
       }
       if (event.getUser() == null || event.getUser().getNeighborhood() == null) {
           throw new ApiException("Event owner neighborhood is required");
       }
       if (!event.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
           throw new ApiException("Event is outside your neighborhood");
       }
       if (event.getUser().getId().equals(userId)) {
           throw new ApiException("Event owner cannot register to own event");
       }
       if (!"ACTIVE".equals(event.getStatus())) {
           throw new ApiException("Event is not active");
       }
       if (eventRegistrationRepository.existsByUserIdAndEventId(userId, eventId)) {
           throw new ApiException("User is already registered for this event");
       }
       if (event.getMaxParticipants() != null && eventRegistrationRepository.countByEventId(eventId) >= event.getMaxParticipants()) {
           throw new ApiException("Event is full");
       }

       EventRegistration registration = new EventRegistration();

       registration.setUser(user);
       registration.setEvent(event);

       if (Boolean.TRUE.equals(event.getIsPaid())) {
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
        User user = familyMember.getUser();
        if (user == null) {
            throw new ApiException("Family member owner not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        if (event.getUser() == null || event.getUser().getNeighborhood() == null) {
            throw new ApiException("Event owner neighborhood is required");
        }
        if (!event.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Event is outside your neighborhood");
        }
        if (event.getUser().getId().equals(user.getId())) {
            throw new ApiException("Event owner family cannot register to own event");
        }
        if (!"ACTIVE".equals(event.getStatus())) {
            throw new ApiException("Event is not active");
        }
        if (eventRegistrationRepository.existsByFamilyMemberIdAndEventId(familyMemberId, eventId)) {
            throw new ApiException("Family member is already registered for this event");
        }
        if (event.getMaxParticipants() != null && eventRegistrationRepository.countByEventId(eventId) >= event.getMaxParticipants()) {
            throw new ApiException("Event is full");
        }
        EventRegistration registration = new EventRegistration();
        registration.setUser(familyMember.getUser());
        registration.setEvent(event);
        registration.setFamilyMember(familyMember);

        if (Boolean.TRUE.equals(event.getIsPaid())) {
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
