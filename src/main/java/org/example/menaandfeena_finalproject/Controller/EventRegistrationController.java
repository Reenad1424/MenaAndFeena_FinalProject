package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.EventRegistrationInDTO;
import org.example.menaandfeena_finalproject.Service.EventRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.example.menaandfeena_finalproject.Model.User;

@RestController
@RequestMapping("/api/v1/event-registration")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllEventRegistrations() {
        return ResponseEntity.status(200).body(eventRegistrationService.getAllEventRegistrations());
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEventRegistration(@PathVariable Integer id,
                                                  @Valid @RequestBody EventRegistrationInDTO eventRegistrationInDTO) {
        eventRegistrationService.updateEventRegistration(id, eventRegistrationInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Event registration updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEventRegistration(@PathVariable Integer id) {
        eventRegistrationService.deleteEventRegistration(id);
        return ResponseEntity.status(200).body(new ApiResponse("Event registration deleted successfully"));
    }

//    @PostMapping("/register/{userId}/{eventId}")
//    public ResponseEntity<?> registerToEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
//        eventRegistrationService.registerToEvent(userId, eventId);
//        return ResponseEntity.status(200).body(new ApiResponse("Registered successfully"));
//    }


    @PostMapping("/register/{eventId}")
    public ResponseEntity<?> registerToEvent(Authentication authentication,
                                             @PathVariable Integer eventId) {

        User user = (User) authentication.getPrincipal();

        eventRegistrationService.registerToEvent(user.getId(), eventId);

        return ResponseEntity.status(200).body(new ApiResponse("Registered successfully"));
    }


//    @PostMapping("/register-family/{familyMemberId}/{eventId}")
//    public ResponseEntity<?> registerFamilyMember(@PathVariable Integer familyMemberId, @PathVariable Integer eventId) {
//        eventRegistrationService.registerFamilyMember(familyMemberId, eventId);
//        return ResponseEntity.status(200).body(new ApiResponse("Family member registered successfully"));
//    }



    @PostMapping("/register-family/{familyMemberId}/{eventId}")
    public ResponseEntity<?> registerFamilyMember(Authentication authentication,
                                                  @PathVariable Integer familyMemberId,
                                                  @PathVariable Integer eventId) {

        User user = (User) authentication.getPrincipal();

        eventRegistrationService.registerFamilyMember(user.getId(), familyMemberId, eventId);

        return ResponseEntity.status(200).body(new ApiResponse("Family member registered successfully"));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyEventRegistrations(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(200).body(eventRegistrationService.getMyEventRegistrations(user.getId()));
    }

    @GetMapping("/event/{eventId}/attendees")
    public ResponseEntity<?> getEventAttendees(Authentication authentication, @PathVariable Integer eventId) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(200).body(eventRegistrationService.getEventAttendees(eventId, user.getId()));
    }

}
