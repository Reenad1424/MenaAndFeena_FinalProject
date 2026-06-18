package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.InquiryMessageInDTO;
import org.example.menaandfeena_finalproject.Service.InquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("/marketplace/{itemId}/create/{requesterId}")
    public ResponseEntity<?> createMarketplaceInquiry(@PathVariable Integer itemId, @PathVariable Integer requesterId) {
        return ResponseEntity.status(200).body(inquiryService.createMarketplaceInquiry(itemId, requesterId));
    }

    @PostMapping("/announcement/{announcementId}/create/{requesterId}")
    public ResponseEntity<?> createAnnouncementInquiry(@PathVariable Integer announcementId, @PathVariable Integer requesterId) {
        return ResponseEntity.status(200).body(inquiryService.createAnnouncementInquiry(announcementId, requesterId));
    }

    @PostMapping("/{inquiryId}/message/{senderId}")
    public ResponseEntity<?> addMessage(@PathVariable Integer inquiryId, @PathVariable Integer senderId, @RequestBody @Valid InquiryMessageInDTO inquiryMessageInDTO) {
        return ResponseEntity.status(200).body(inquiryService.addMessage(inquiryId, senderId, inquiryMessageInDTO));
    }

    @GetMapping("/{inquiryId}/messages/{userId}")
    public ResponseEntity<?> getMessages(@PathVariable Integer inquiryId, @PathVariable Integer userId) {
        return ResponseEntity.status(200).body(inquiryService.getMessages(inquiryId, userId));
    }

    @GetMapping("/my-inquiries/{userId}")
    public ResponseEntity<?> getMyInquiries(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(inquiryService.getMyInquiries(userId));
    }

    @GetMapping("/received/{userId}")
    public ResponseEntity<?> getReceivedInquiries(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(inquiryService.getReceivedInquiries(userId));
    }

    @PutMapping("/{inquiryId}/resolve/{userId}")
    public ResponseEntity<?> resolveInquiry(@PathVariable Integer inquiryId, @PathVariable Integer userId) {
        inquiryService.resolveInquiry(inquiryId, userId);
        return ResponseEntity.status(200).body(new ApiResponse("Inquiry resolved"));
    }
}
