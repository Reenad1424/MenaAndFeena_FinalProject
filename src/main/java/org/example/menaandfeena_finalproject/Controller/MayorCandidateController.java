package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.MayorCandidateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mayor-candidates")
@RequiredArgsConstructor
public class MayorCandidateController {

    private final MayorCandidateService mayorCandidateService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllMayorCandidates() {
        return ResponseEntity.status(200).body(
                mayorCandidateService.getAllMayorCandidates()
        );
    }

    @PostMapping("/apply/round/{roundId}")
    public ResponseEntity<?> applyForMayorCandidacy(
            @AuthenticationPrincipal User user,
            @PathVariable Integer roundId
    ) {
        mayorCandidateService.applyForMayorCandidacy(user.getId(), roundId);

        return ResponseEntity.status(201).body(
                new ApiResponse("تم تقديم ترشيحك لمنصب عمدة الحي بنجاح")
        );
    }

    @GetMapping("/round/{roundId}")
    public ResponseEntity<?> getCandidatesByRound(
            @PathVariable Integer roundId
    ) {
        return ResponseEntity.status(200).body(
                mayorCandidateService.getCandidatesByRound(roundId)
        );
    }

    @GetMapping("/round/{roundId}/dashboard")
    public ResponseEntity<?> getElectionDashboard(
            @PathVariable Integer roundId
    ) {
        return ResponseEntity.status(200).body(
                mayorCandidateService.getElectionDashboard(roundId)
        );
    }

    @GetMapping("/profile/{candidateId}")
    public ResponseEntity<?> getCandidateProfile(
            @PathVariable Integer candidateId
    ) {
        return ResponseEntity.status(200).body(
                mayorCandidateService.getCandidateProfile(candidateId)
        );
    }

    @PutMapping("/update/{candidateId}")
    public ResponseEntity<?> updateMayorCandidate(
            @PathVariable Integer candidateId
    ) {
        mayorCandidateService.updateMayorCandidate(candidateId);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم تحديث بيانات المرشح بنجاح")
        );
    }

    @DeleteMapping("/delete/{candidateId}")
    public ResponseEntity<?> deleteMayorCandidate(
            @PathVariable Integer candidateId
    ) {
        mayorCandidateService.deleteMayorCandidate(candidateId);

        return ResponseEntity.status(200).body(
                new ApiResponse("تم حذف المرشح بنجاح")
        );
    }
}