package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.InquiryMessageInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.InquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("/marketplace/{itemId}/create")
    public ResponseEntity<?> createMarketplaceInquiry(@PathVariable Integer itemId,
                                                      @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(inquiryService.createMarketplaceInquiry(itemId, user.getId()));
    }

    @PostMapping("/announcement/{announcementId}/create")
    public ResponseEntity<?> createAnnouncementInquiry(@PathVariable Integer announcementId,
                                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(inquiryService.createAnnouncementInquiry(announcementId, user.getId()));
    }

    @PostMapping("/{inquiryId}/message")
    public ResponseEntity<?> addMessage(@PathVariable Integer inquiryId,
                                        @AuthenticationPrincipal User user,
                                        @RequestBody @Valid InquiryMessageInDTO inquiryMessageInDTO) {
        return ResponseEntity.status(200).body(inquiryService.addMessage(inquiryId, user.getId(), inquiryMessageInDTO));
    }

    @GetMapping("/{inquiryId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Integer inquiryId,
                                         @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(inquiryService.getMessages(inquiryId, user.getId()));
    }

    @GetMapping("/my-inquiries")
    public ResponseEntity<?> getMyInquiries(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(inquiryService.getMyInquiries(user.getId()));
    }

    @GetMapping("/received")
    public ResponseEntity<?> getReceivedInquiries(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(inquiryService.getReceivedInquiries(user.getId()));
    }

    @PutMapping("/{inquiryId}/resolve")
    public ResponseEntity<?> resolveInquiry(@PathVariable Integer inquiryId,
                                            @AuthenticationPrincipal User user) {
        inquiryService.resolveInquiry(inquiryId, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Inquiry resolved"));
    }
}
