package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.MayorProfileInDTO;
import org.example.menaandfeena_finalproject.Service.MayorProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mayor-profile")
@RequiredArgsConstructor
public class MayorProfileController {
    private final MayorProfileService mayorProfileService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllMayorProfiles() {
        return ResponseEntity.status(200)
                .body(mayorProfileService.getAllMayorProfiles());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMayorProfile(@Valid @RequestBody MayorProfileInDTO mayorProfileInDTO) {

        mayorProfileService.addMayorProfile(mayorProfileInDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Mayor profile added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMayorProfile(@PathVariable Integer id, @Valid @RequestBody MayorProfileInDTO mayorProfileInDTO) {

        mayorProfileService.updateMayorProfile(id, mayorProfileInDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Mayor profile updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMayorProfile(@PathVariable Integer id) {

        mayorProfileService.deleteMayorProfile(id);

        return ResponseEntity.status(200).body(new ApiResponse("Mayor profile deleted successfully"));
    }

        @GetMapping("/analytics/{id}")
        public ResponseEntity<?> getAnalytics(@PathVariable Integer id) {
            return ResponseEntity.status(200).body(mayorProfileService.getMayorAnalytics(id));
        }

        @GetMapping("/reports/{id}")
        public ResponseEntity<?> getReports(@PathVariable Integer id) {
            return ResponseEntity.status(200).body(mayorProfileService.getMayorReports(id));
        }
    @GetMapping("/weekly/{id}")
    public void weekly(@PathVariable Integer id) {
        mayorProfileService.sendWeeklyReport(id);
    }

    @GetMapping("/performance/{id}")
    public void performance(@PathVariable Integer id) {
        mayorProfileService.sendPerformanceReport(id);
    }

    @GetMapping("/satisfaction/{id}")
    public void satisfaction(@PathVariable Integer id) {
        mayorProfileService.sendSatisfactionReport(id);
    }

    @PostMapping("/resend-appointment-email/{mayorId}")
    public ResponseEntity<?> resendMayorAppointmentEmail(
            @PathVariable Integer mayorId
    ) {
        mayorProfileService.resendMayorAppointmentEmail(mayorId);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم إعادة إرسال رسالة تنصيب العمدة")
        );
    }
    }

