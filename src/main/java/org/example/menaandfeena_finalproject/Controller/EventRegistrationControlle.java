package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.EventRegistration;
import org.example.menaandfeena_finalproject.Service.EventRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/event-registration")
@RequiredArgsConstructor
public class EventRegistrationControlle {

    private final EventRegistrationService eventRegistrationService;

    @GetMapping("/get")
    public ResponseEntity getAllEventRegistrations() {
        return ResponseEntity.status(200).body(eventRegistrationService.getAllEventRegistrations());
    }

    @PostMapping("/add")
    public ResponseEntity addEventRegistration(@Valid @RequestBody EventRegistration eventRegistration) {
        eventRegistrationService.addEventRegistration(eventRegistration);
        return ResponseEntity.status(200).body(new ApiResponse("Event registration added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateEventRegistration(@PathVariable Integer id,
                                                  @Valid @RequestBody EventRegistration eventRegistration) {
        eventRegistrationService.updateEventRegistration(id, eventRegistration);
        return ResponseEntity.status(200).body(new ApiResponse("Event registration updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteEventRegistration(@PathVariable Integer id) {
        eventRegistrationService.deleteEventRegistration(id);
        return ResponseEntity.status(200).body(new ApiResponse("Event registration deleted successfully"));
    }

    @PostMapping("/register/{userId}/{eventId}")
    public ResponseEntity registerToEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        eventRegistrationService.registerToEvent(userId, eventId);
        return ResponseEntity.status(200).body(new ApiResponse("Registered successfully"));
    }


    @PostMapping("/register-family/{familyMemberId}/{eventId}")
    public ResponseEntity registerFamilyMember(@PathVariable Integer familyMemberId, @PathVariable Integer eventId) {
        eventRegistrationService.registerFamilyMember(familyMemberId, eventId);
        return ResponseEntity.status(200).body(new ApiResponse("Family member registered successfully"));
    }










}
