package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.Out.EventAttendeeOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.EventTicketOutDTO;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.EventRegistration;
import org.example.menaandfeena_finalproject.Model.FamilyMember;
import org.example.menaandfeena_finalproject.Model.Ticket;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.EventRegistrationRepository;
import org.example.menaandfeena_finalproject.Repository.TicketRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final UserRepository userRepository;
    private final WhatsAppService whatsAppService;

    public Ticket createTicketIfMissing(EventRegistration registration) {
        Ticket existingTicket = ticketRepository.findByEventRegistration_Id(registration.getId());
        if (existingTicket != null) {
            return existingTicket;
        }

        Ticket ticket = new Ticket();
        ticket.setTicketCode(generateTicketCode());
        ticket.setCheckedIn(false);
        ticket.setCheckedInAt(null);
        ticket.setIssuedAt(LocalDateTime.now());
        ticket.setEventRegistration(registration);

        Ticket savedTicket = ticketRepository.save(ticket);
        trySendTicketWhatsApp(savedTicket);

        return savedTicket;
    }

    public EventTicketOutDTO getTicket(Integer registrationId, Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        EventRegistration registration = eventRegistrationRepository.findEventRegistrationById(registrationId);
        if (registration == null) {
            throw new ApiException("Event registration not found");
        }

        if (registration.getUser() == null || !registration.getUser().getId().equals(userId)) {
            throw new ApiException("Event registration does not belong to this user");
        }

        if (!"CONFIRMED".equals(registration.getStatus())) {
            throw new ApiException("Ticket is available only for confirmed registrations");
        }

        return toTicketOutDTO(createTicketIfMissing(registration));
    }

    public EventTicketOutDTO checkInByTicketCode(String ticketCode, Integer ownerId) {
        User owner = userRepository.findUserById(ownerId);
        if (owner == null) {
            throw new ApiException("User not found");
        }

        Ticket ticket = ticketRepository.findTicketByTicketCode(ticketCode);
        if (ticket == null) {
            throw new ApiException("Ticket not found");
        }

        EventRegistration registration = ticket.getEventRegistration();
        if (registration == null) {
            throw new ApiException("Event registration not found");
        }

        Event event = registration.getEvent();
        if (event == null) {
            throw new ApiException("Event not found");
        }

        if (event.getUser() == null || !event.getUser().getId().equals(ownerId)) {
            throw new ApiException("Only the event owner can check in attendees");
        }

        if (!"CONFIRMED".equals(registration.getStatus())) {
            throw new ApiException("Only confirmed registrations can be checked in");
        }

        if (Boolean.TRUE.equals(ticket.getCheckedIn())) {
            throw new ApiException("Ticket is already checked in");
        }

        ticket.setCheckedIn(true);
        ticket.setCheckedInAt(LocalDateTime.now());

        return toTicketOutDTO(ticketRepository.save(ticket));
    }

    public EventAttendeeOutDTO toAttendeeOutDTO(EventRegistration registration) {
        User user = registration.getUser();
        FamilyMember familyMember = registration.getFamilyMember();
        Ticket ticket = ticketRepository.findByEventRegistration_Id(registration.getId());

        return new EventAttendeeOutDTO(
                registration.getId(),
                registration.getStatus(),
                registration.getRegisteredAt(),
                ticket == null ? null : ticket.getTicketCode(),
                ticket != null && Boolean.TRUE.equals(ticket.getCheckedIn()),
                ticket == null ? null : ticket.getCheckedInAt(),
                user == null ? null : user.getId(),
                user == null ? null : user.getFullName(),
                familyMember == null ? null : familyMember.getId(),
                familyMember == null ? null : familyMember.getName()
        );
    }

    private EventTicketOutDTO toTicketOutDTO(Ticket ticket) {
        EventRegistration registration = ticket.getEventRegistration();
        if (registration == null) {
            throw new ApiException("Event registration not found");
        }

        Event event = registration.getEvent();
        User user = registration.getUser();
        FamilyMember familyMember = registration.getFamilyMember();

        return new EventTicketOutDTO(
                registration.getId(),
                ticket.getTicketCode(),
                registration.getStatus(),
                registration.getRegisteredAt(),
                ticket.getCheckedIn(),
                ticket.getCheckedInAt(),
                event == null ? null : event.getId(),
                event == null ? null : event.getTitle(),
                event == null ? null : event.getDate(),
                event == null ? null : event.getLocation(),
                user == null ? null : user.getId(),
                user == null ? null : user.getFullName(),
                familyMember == null ? null : familyMember.getId(),
                familyMember == null ? null : familyMember.getName()
        );
    }

    private String generateTicketCode() {
        String ticketCode;
        do {
            ticketCode = "TICKET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (ticketRepository.existsByTicketCode(ticketCode));

        return ticketCode;
    }

    private void trySendTicketWhatsApp(Ticket ticket) {
        try {
            EventRegistration registration = ticket.getEventRegistration();
            if (registration == null || registration.getUser() == null) {
                return;
            }

            User user = registration.getUser();
            if (user.getPhone() == null || user.getPhone().isBlank()) {
                return;
            }

            Event event = registration.getEvent();
            String eventTitle = event == null ? "your event" : event.getTitle();

            String message =
                    "You have registered successfully.\n\n" +
                            "Event: " + eventTitle + "\n" +
                            "Ticket code: " + ticket.getTicketCode();

            whatsAppService.sendWhatsAppMessage(user.getPhone(), message);
        } catch (Exception e) {
            Integer ticketId = ticket == null ? null : ticket.getId();
            log.error("Ticket WhatsApp message failed for ticket id {}. Registration confirmation was kept.", ticketId, e);
        }
    }
}
