package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.Announcement;
import org.example.menaandfeena_finalproject.Service.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/get")
    public ResponseEntity getAllAnnouncements() {
        return ResponseEntity.status(200).body(announcementService.getAllAnnouncements());
    }

    @PostMapping("/add")
    public ResponseEntity addAnnouncement(@Valid @RequestBody Announcement announcement) {

        announcementService.addAnnouncement(announcement);
        return ResponseEntity.status(200).body(new ApiResponse("Announcement added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateAnnouncement(@PathVariable Integer id, @Valid @RequestBody Announcement announcement) {

        announcementService.updateAnnouncement(id, announcement);
        return ResponseEntity.status(200).body(new ApiResponse("Announcement updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteAnnouncement(@PathVariable Integer id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.status(200).body(new ApiResponse("Announcement deleted successfully"));
    }


    @PostMapping("/create/{userId}")
    public ResponseEntity createAnnouncement(@PathVariable Integer userId, @Valid @RequestBody Announcement announcement) {
        announcementService.createAnnouncement(userId, announcement);
        return ResponseEntity.status(200).body(new ApiResponse("Announcement created successfully"));

    }


    @GetMapping("/search")
    public ResponseEntity searchAnnouncements(@RequestParam String keyword) {
        return ResponseEntity.status(200).body(announcementService.searchAnnouncements(keyword));
    }


    @GetMapping("/{id}")
    public ResponseEntity getAnnouncementById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(announcementService.getAnnouncementById(id));
    }


    @GetMapping("/contact-publisher/{announcementId}")
    public ResponseEntity getPublisherContact(@PathVariable Integer announcementId) {
        return ResponseEntity.status(200).body(announcementService.getPublisherContact(announcementId));
    }

}
