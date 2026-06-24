package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.AnnouncementInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllAnnouncements() {
        return ResponseEntity.status(200).body(announcementService.getAllAnnouncements());
    }

    // إعلانات المستخدم الحالي (صفحة الملف الشخصي: "إعلاناتي").
    @GetMapping("/my")
    public ResponseEntity<?> getMyAnnouncements(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(200).body(announcementService.getMyAnnouncements(user.getId()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAnnouncement(Authentication authentication,
                                                @PathVariable Integer id,
                                                @Valid @RequestBody AnnouncementInDTO announcementInDTO) {

        User user = (User) authentication.getPrincipal();

        announcementService.updateAnnouncement(id, user.getId(), announcementInDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Announcement updated successfully"));
    }




    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAnnouncement(Authentication authentication,
                                                @PathVariable Integer id) {

        User user = (User) authentication.getPrincipal();

        announcementService.deleteAnnouncement(id, user.getId());

        return ResponseEntity.status(200).body(new ApiResponse("Announcement deleted successfully"));
    }


    @PostMapping("/create")
    public ResponseEntity<?> createAnnouncement(Authentication authentication,
                                                @Valid @RequestBody AnnouncementInDTO announcementInDTO) {

        User user = (User) authentication.getPrincipal();

        announcementService.createAnnouncement(user.getId(), announcementInDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Announcement created successfully"));
    }



    @PostMapping("/moderate/{announcementId}")
    public ResponseEntity<?> moderateAnnouncement(Authentication authentication,
                                                  @PathVariable Integer announcementId) {

        User user = (User) authentication.getPrincipal();

        announcementService.moderateAnnouncement(announcementId, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Announcement moderated successfully"));
    }




    @GetMapping("/search")
    public ResponseEntity<?> searchAnnouncements(@RequestParam String keyword) {
        return ResponseEntity.status(200).body(announcementService.searchAnnouncements(keyword));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getAnnouncementById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(announcementService.getAnnouncementById(id));
    }


    @GetMapping("/contact-publisher/{announcementId}")
    public ResponseEntity<?> getPublisherContact(@PathVariable Integer announcementId) {
        return ResponseEntity.status(200).body(announcementService.getPublisherContact(announcementId));
    }


}
