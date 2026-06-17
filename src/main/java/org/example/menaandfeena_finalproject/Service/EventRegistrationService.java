package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.EventRegistration;
import org.example.menaandfeena_finalproject.Repository.EventRegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {
    private final EventRegistrationRepository eventRegistrationRepository;

    public List<EventRegistration> getAllEventRegistrations() {
        return eventRegistrationRepository.findAll();
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
}
