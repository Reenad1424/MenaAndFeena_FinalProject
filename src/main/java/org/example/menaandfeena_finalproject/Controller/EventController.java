package org.example.menaandfeena_finalproject.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.EventInDTO;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Service.EventService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.example.menaandfeena_finalproject.Model.User;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllEvents() {
        return ResponseEntity.status(200).body(eventService.getAllEvents());
    }

@PutMapping("/update/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Integer id, @Valid @RequestBody EventInDTO eventInDTO) {
        eventService.updateEvent(id, eventInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Event updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
        return ResponseEntity.status(200).body(new ApiResponse("Event deleted successfully"));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingEvents() {
        return ResponseEntity.status(200).body(eventService.getUpcomingEvents());
    }
    @GetMapping("/previous")
    public ResponseEntity<?> getPreviousEvents() {
        return ResponseEntity.status(200).body(eventService.getPreviousEvents());
    }


    @GetMapping("/date/{date}")
    public ResponseEntity<?> getEventsByDate(@PathVariable LocalDate date) {
        return ResponseEntity.status(200).body(eventService.getEventsByDate(date));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(eventService.getEventById(id));
    }

    // كل الميزات المعرّفة مسبقاً (لعرضها كـ checkboxes في الفرونت إند).
    @GetMapping("/features")
    public ResponseEntity<?> getAllFeatures() {
        return ResponseEntity.status(200).body(eventService.getAllFeatures());
    }

    // الميزات المختارة لفعالية معيّنة.
    @GetMapping("/{eventId}/features")
    public ResponseEntity<?> getEventFeatures(@PathVariable Integer eventId) {
        return ResponseEntity.status(200).body(eventService.getEventFeatures(eventId));
    }

    // برنامج الفعالية مرتباً حسب sortOrder ثم الوقت.
    @GetMapping("/{eventId}/schedule")
    public ResponseEntity<?> getEventSchedule(@PathVariable Integer eventId) {
        return ResponseEntity.status(200).body(eventService.getEventSchedule(eventId));
    }



//
//    @PostMapping("/create/{userId}")
//    public ResponseEntity<?> createEvent(@PathVariable Integer userId, @Valid @RequestBody EventInDTO eventInDTO) {
//        eventService.createEvent(userId, eventInDTO);
//        return ResponseEntity.status(200).body(new ApiResponse("Event created successfully"));
//    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(Authentication authentication,
                                         @Valid @RequestBody EventInDTO eventInDTO) {

        User user = (User) authentication.getPrincipal();
        eventService.createEvent(user.getId(), eventInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Event created successfully"));
    }

    @PostMapping(value = "/{eventId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadEventImage(@AuthenticationPrincipal User user,
                                              @PathVariable Integer eventId,
                                              @RequestParam("image") MultipartFile image) {
        return ResponseEntity.status(200).body(eventService.uploadEventImage(user.getId(), eventId, image));
    }



//    @GetMapping("/recommend/{userId}")
//    public ResponseEntity<?> recommendEvent(@PathVariable Integer userId) {
//
//        return ResponseEntity.status(200).body(eventService.recommendEventForUser(userId));
//    }


    @GetMapping("/recommend")
    public ResponseEntity<?> recommendEvent(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.status(200).body(eventService.recommendEventForUser(user.getId()));
    }

    @GetMapping("/weekend-plan")
    public ResponseEntity<?> generateWeekendFamilyPlan(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.status(200).body(eventService.generateWeekendFamilyPlan(user.getId()));
    }







}
