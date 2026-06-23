package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.EventScheduleInDTO;
import org.example.menaandfeena_finalproject.Service.EventScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/event-schedules")
@RequiredArgsConstructor
public class EventScheduleController {

    private final EventScheduleService eventScheduleService;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<?> addScheduleItem(@PathVariable Integer eventId,
                                             @RequestBody @Valid EventScheduleInDTO eventScheduleInDTO) {
        return ResponseEntity.status(201).body(eventScheduleService.addScheduleItem(eventId, eventScheduleInDTO));
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateScheduleItem(@PathVariable Integer scheduleId,
                                                @RequestBody @Valid EventScheduleInDTO eventScheduleInDTO) {
        return ResponseEntity.status(200).body(eventScheduleService.updateScheduleItem(scheduleId, eventScheduleInDTO));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteScheduleItem(@PathVariable Integer scheduleId) {
        eventScheduleService.deleteScheduleItem(scheduleId);
        return ResponseEntity.status(200).body(new ApiResponse("Event schedule item deleted successfully"));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getEventSchedule(@PathVariable Integer eventId) {
        return ResponseEntity.status(200).body(eventScheduleService.getEventSchedule(eventId));
    }
}
