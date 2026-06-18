package org.example.menaandfeena_finalproject.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/get")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.status(200).body(eventService.getAllEvents());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addEvent(@Valid @RequestBody Event event) {
        eventService.addEvent(event);
        return ResponseEntity.status(200).body(new ApiResponse("Event added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateEvent(@PathVariable Integer id, @Valid @RequestBody Event event) {
        eventService.updateEvent(id, event);
        return ResponseEntity.status(200).body(new ApiResponse("Event updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
        return ResponseEntity.status(200).body(new ApiResponse("Event deleted successfully"));
    }

    @GetMapping("/upcoming")
    public ResponseEntity getUpcomingEvents() {
        return ResponseEntity.status(200).body(eventService.getUpcomingEvents());
    }
    @GetMapping("/previous")
    public ResponseEntity getPreviousEvents() {
        return ResponseEntity.status(200).body(eventService.getPreviousEvents());
    }


    @GetMapping("/date/{date}")
    public ResponseEntity getEventsByDate(@PathVariable LocalDate date) {
        return ResponseEntity.status(200).body(eventService.getEventsByDate(date));
    }


    @GetMapping("/{id}")
    public ResponseEntity getEventById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(eventService.getEventById(id));
    }

}
