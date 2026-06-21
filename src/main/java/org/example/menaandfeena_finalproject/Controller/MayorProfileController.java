package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.MayorProfileInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.MayorProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mayor-profiles")
@RequiredArgsConstructor
public class MayorProfileController {

    private final MayorProfileService mayorProfileService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllMayorProfiles() {
        return ResponseEntity.status(200).body(
                mayorProfileService.getAllMayorProfiles()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMayorProfile(
            @Valid @RequestBody MayorProfileInDTO mayorProfileInDTO
    ) {
        mayorProfileService.addMayorProfile(mayorProfileInDTO);

        return ResponseEntity.status(201).body(
                new ApiResponse("Mayor profile added successfully")
        );
    }

    @PutMapping("/update/{mayorProfileId}")
    public ResponseEntity<?> updateMayorProfile(
            @PathVariable Integer mayorProfileId,
            @Valid @RequestBody MayorProfileInDTO mayorProfileInDTO
    ) {
        mayorProfileService.updateMayorProfile(
                mayorProfileId,
                mayorProfileInDTO
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("Mayor profile updated successfully")
        );
    }

    @DeleteMapping("/delete/{mayorProfileId}")
    public ResponseEntity<?> deleteMayorProfile(
            @PathVariable Integer mayorProfileId
    ) {
        mayorProfileService.deleteMayorProfile(mayorProfileId);

        return ResponseEntity.status(200).body(
                new ApiResponse("Mayor profile deleted successfully")
        );
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(200).body(
                mayorProfileService.getMayorAnalytics(user.getId())
        );
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getReports(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(200).body(
                mayorProfileService.getMayorReports(user.getId())
        );
    }

    @GetMapping("/weekly")
    public ResponseEntity<?> weekly(
            @AuthenticationPrincipal User user
    ) {
        mayorProfileService.sendWeeklyReport(user.getId());

        return ResponseEntity.status(200).body(
                new ApiResponse("تم إرسال التقرير الأسبوعي للعمدة")
        );
    }

    @GetMapping("/performance")
    public ResponseEntity<?> performance(
            @AuthenticationPrincipal User user
    ) {
        mayorProfileService.sendPerformanceReport(user.getId());

        return ResponseEntity.status(200).body(
                new ApiResponse("تم إرسال تقرير أداء الحي للعمدة")
        );
    }

    @GetMapping("/satisfaction")
    public ResponseEntity<?> satisfaction(
            @AuthenticationPrincipal User user
    ) {
        mayorProfileService.sendSatisfactionReport(user.getId());

        return ResponseEntity.status(200).body(
                new ApiResponse("تم إرسال تقرير رضا السكان للعمدة")
        );
    }

    @PostMapping("/resend-appointment-email")
    public ResponseEntity<?> resendMayorAppointmentEmail(
            @AuthenticationPrincipal User user
    ) {
        mayorProfileService.resendMayorAppointmentEmail(user.getId());

        return ResponseEntity.status(200).body(
                new ApiResponse("تم إعادة إرسال رسالة تنصيب العمدة")
        );
    }
}